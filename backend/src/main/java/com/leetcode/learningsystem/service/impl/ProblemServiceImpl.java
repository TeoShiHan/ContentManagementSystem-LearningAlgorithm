package com.leetcode.learningsystem.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leetcode.learningsystem.dto.*;
import com.leetcode.learningsystem.model.*;
import com.leetcode.learningsystem.service.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProblemServiceImpl implements ProblemService {

    private final FileStorageService fileStorageService;
    private final FileTypeRegistry fileTypeRegistry;
    private final ObjectMapper objectMapper;

    public ProblemServiceImpl(FileStorageService fileStorageService,
                              FileTypeRegistry fileTypeRegistry,
                              ObjectMapper objectMapper) {
        this.fileStorageService = fileStorageService;
        this.fileTypeRegistry = fileTypeRegistry;
        this.objectMapper = objectMapper;
    }

    @Override
    public ProblemResponse createProblem(ProblemRequest request) {
        String folderName = buildFolderName(request.getProblemCode(), request.getTitle());
        Path storageRoot = fileStorageService.getStorageRoot();
        Path problemDir = storageRoot.resolve(folderName);

        if (Files.exists(problemDir)) {
            // Append a counter to avoid collision
            int counter = 2;
            while (Files.exists(storageRoot.resolve(folderName + "_" + counter))) {
                counter++;
            }
            folderName = folderName + "_" + counter;
            problemDir = storageRoot.resolve(folderName);
        }

        try {
            Files.createDirectories(problemDir);
            writeProblemJson(problemDir, request);
            return toResponse(folderName, readProblemJson(problemDir), scanFiles(problemDir));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create problem", e);
        }
    }

    @Override
    public ProblemResponse updateProblem(String id, ProblemRequest request) {
        Path problemDir = fileStorageService.getStorageRoot().resolve(id);
        if (!Files.isDirectory(problemDir)) {
            throw new RuntimeException("Problem not found: " + id);
        }
        try {
            writeProblemJson(problemDir, request);
            return toResponse(id, readProblemJson(problemDir), scanFiles(problemDir));
        } catch (IOException e) {
            throw new RuntimeException("Failed to update problem", e);
        }
    }

    @Override
    public ProblemResponse getProblem(String id) {
        Path problemDir = fileStorageService.getStorageRoot().resolve(id);
        if (!Files.isDirectory(problemDir)) {
            throw new RuntimeException("Problem not found: " + id);
        }
        try {
            return toResponse(id, readProblemJson(problemDir), scanFiles(problemDir));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read problem", e);
        }
    }

    @Override
    public List<ProblemResponse> getAllProblems() {
        List<ProblemResponse> results = new ArrayList<>();
        Path storageRoot = fileStorageService.getStorageRoot();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storageRoot, Files::isDirectory)) {
            for (Path dir : stream) {
                Path metaFile = dir.resolve("problem.json");
                if (!Files.exists(metaFile)) continue;
                try {
                    Problem problem = readProblemJson(dir);
                    results.add(toResponse(dir.getFileName().toString(), problem, scanFiles(dir)));
                } catch (Exception e) {
                    // Skip malformed entries
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to scan storage directory", e);
        }
        return results;
    }

    @Override
    public List<ProblemResponse> queryProblems(Difficulty difficulty, QuestionType questionType,
                                                Integer minRank, Integer maxRank, String search) {
        return getAllProblems().stream()
                .filter(p -> difficulty == null || p.getDifficulty() == difficulty)
                .filter(p -> questionType == null || p.getQuestionType() == questionType)
                .filter(p -> minRank == null || p.getCustomRank() >= minRank)
                .filter(p -> maxRank == null || p.getCustomRank() <= maxRank)
                .filter(p -> search == null || search.isBlank() ||
                        p.getTitle().toLowerCase().contains(search.toLowerCase()) ||
                        p.getProblemCode().toLowerCase().contains(search.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProblem(String id) {
        fileStorageService.deleteProblemDirectory(id);
    }

    @Override
    public StatsResponse getStats() {
        List<ProblemResponse> all = getAllProblems();
        long total = all.size();
        long easy = all.stream().filter(p -> p.getDifficulty() == Difficulty.EASY).count();
        long medium = all.stream().filter(p -> p.getDifficulty() == Difficulty.MEDIUM).count();
        long hard = all.stream().filter(p -> p.getDifficulty() == Difficulty.HARD).count();

        Map<String, Long> byType = new LinkedHashMap<>();
        for (QuestionType qt : QuestionType.values()) {
            long count = all.stream().filter(p -> p.getQuestionType() == qt).count();
            if (count > 0) byType.put(qt.name(), count);
        }

        double avgRank = all.stream().mapToInt(ProblemResponse::getCustomRank).average().orElse(0.0);

        return StatsResponse.builder()
                .totalProblems(total)
                .easyCount(easy)
                .mediumCount(medium)
                .hardCount(hard)
                .byQuestionType(byType)
                .averageCustomRank(Math.round(avgRank * 100.0) / 100.0)
                .build();
    }

    // ---- helpers ----

    private Problem readProblemJson(Path problemDir) throws IOException {
        return objectMapper.readValue(problemDir.resolve("problem.json").toFile(), Problem.class);
    }

    private void writeProblemJson(Path problemDir, ProblemRequest request) throws IOException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("problemCode", request.getProblemCode());
        data.put("title", request.getTitle());
        data.put("leetcodeLink", request.getLeetcodeLink());
        data.put("questionType", request.getQuestionType());
        data.put("solution", request.getSolution());
        data.put("difficulty", request.getDifficulty());
        data.put("customRank", request.getCustomRank());
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(problemDir.resolve("problem.json").toFile(), data);
    }

    private List<ProblemFileResponse> scanFiles(Path problemDir) throws IOException {
        List<ProblemFileResponse> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(problemDir)) {
            for (Path file : stream) {
                if (Files.isDirectory(file)) continue;
                String name = file.getFileName().toString();
                if ("problem.json".equals(name)) continue;
                String ext = getExtension(name);
                files.add(ProblemFileResponse.builder()
                        .id(name)
                        .fileName(name)
                        .fileExtension(ext)
                        .filePath(file.toAbsolutePath().toString())
                        .fileSize(Files.size(file))
                        .openWith(fileTypeRegistry.getOpenWith(ext))
                        .build());
            }
        }
        return files;
    }

    private ProblemResponse toResponse(String folderName, Problem problem, List<ProblemFileResponse> files) {
        return ProblemResponse.builder()
                .id(folderName)
                .problemCode(problem.getProblemCode())
                .title(problem.getTitle())
                .leetcodeLink(problem.getLeetcodeLink())
                .questionType(problem.getQuestionType())
                .solution(problem.getSolution())
                .difficulty(problem.getDifficulty())
                .customRank(problem.getCustomRank())
                .files(files)
                .build();
    }

    private String buildFolderName(String code, String title) {
        String raw = code + "-" + title;
        return raw.replaceAll("[^a-zA-Z0-9._-]", "_")
                  .replaceAll("_+", "_")
                  .replaceAll("^_|_$", "")
                  .toLowerCase();
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot > 0 ? filename.substring(dot + 1).toLowerCase() : "";
    }
}
