package com.leetcode.learningsystem.service.impl;

import com.leetcode.learningsystem.dto.*;
import com.leetcode.learningsystem.model.*;
import com.leetcode.learningsystem.repository.*;
import com.leetcode.learningsystem.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemFileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final FileTypeRegistry fileTypeRegistry;

    public ProblemServiceImpl(ProblemRepository problemRepository,
                              ProblemFileRepository fileRepository,
                              FileStorageService fileStorageService,
                              FileTypeRegistry fileTypeRegistry) {
        this.problemRepository = problemRepository;
        this.fileRepository = fileRepository;
        this.fileStorageService = fileStorageService;
        this.fileTypeRegistry = fileTypeRegistry;
    }

    @Override
    public ProblemResponse createProblem(ProblemRequest request) {
        Problem problem = Problem.builder()
                .problemCode(request.getProblemCode())
                .title(request.getTitle())
                .leetcodeLink(request.getLeetcodeLink())
                .questionType(request.getQuestionType())
                .solution(request.getSolution())
                .difficulty(request.getDifficulty())
                .customRank(request.getCustomRank())
                .build();

        problem = problemRepository.save(problem);
        return toResponse(problem);
    }

    @Override
    public ProblemResponse updateProblem(Long id, ProblemRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + id));

        problem.setProblemCode(request.getProblemCode());
        problem.setTitle(request.getTitle());
        problem.setLeetcodeLink(request.getLeetcodeLink());
        problem.setQuestionType(request.getQuestionType());
        problem.setSolution(request.getSolution());
        problem.setDifficulty(request.getDifficulty());
        problem.setCustomRank(request.getCustomRank());

        problem = problemRepository.save(problem);
        return toResponse(problem);
    }

    @Override
    public ProblemResponse getProblem(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + id));
        return toResponse(problem);
    }

    @Override
    public List<ProblemResponse> getAllProblems() {
        return problemRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProblemResponse> queryProblems(Difficulty difficulty, QuestionType questionType,
                                                Integer minRank, Integer maxRank, String search) {
        return problemRepository.findByFilters(difficulty, questionType, minRank, maxRank, search)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProblem(Long id) {
        fileStorageService.deleteProblemDirectory(id);
        fileRepository.deleteByProblemId(id);
        problemRepository.deleteById(id);
    }

    @Override
    public StatsResponse getStats() {
        long total = problemRepository.count();
        long easy = problemRepository.countByDifficulty(Difficulty.EASY);
        long medium = problemRepository.countByDifficulty(Difficulty.MEDIUM);
        long hard = problemRepository.countByDifficulty(Difficulty.HARD);

        Map<String, Long> byType = new LinkedHashMap<>();
        for (QuestionType qt : QuestionType.values()) {
            long count = problemRepository.countByQuestionType(qt);
            if (count > 0) {
                byType.put(qt.name(), count);
            }
        }

        double avgRank = problemRepository.findAll().stream()
                .mapToInt(Problem::getCustomRank)
                .average()
                .orElse(0.0);

        return StatsResponse.builder()
                .totalProblems(total)
                .easyCount(easy)
                .mediumCount(medium)
                .hardCount(hard)
                .byQuestionType(byType)
                .averageCustomRank(Math.round(avgRank * 100.0) / 100.0)
                .build();
    }

    private ProblemResponse toResponse(Problem problem) {
        List<ProblemFile> files = fileRepository.findByProblemId(problem.getId());
        List<ProblemFileResponse> fileResponses = files.stream()
                .map(this::toFileResponse)
                .collect(Collectors.toList());

        return ProblemResponse.builder()
                .id(problem.getId())
                .problemCode(problem.getProblemCode())
                .title(problem.getTitle())
                .leetcodeLink(problem.getLeetcodeLink())
                .questionType(problem.getQuestionType())
                .solution(problem.getSolution())
                .difficulty(problem.getDifficulty())
                .customRank(problem.getCustomRank())
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .files(fileResponses)
                .build();
    }

    private ProblemFileResponse toFileResponse(ProblemFile file) {
        return ProblemFileResponse.builder()
                .id(file.getId())
                .fileName(file.getFileName())
                .fileExtension(file.getFileExtension())
                .filePath(file.getFilePath())
                .fileSize(file.getFileSize())
                .createdAt(file.getCreatedAt())
                .openWith(fileTypeRegistry.getOpenWith(file.getFileExtension()))
                .build();
    }
}
