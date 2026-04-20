package com.leetcode.learningsystem.service.impl;

import com.leetcode.learningsystem.model.ProblemFile;
import com.leetcode.learningsystem.repository.ProblemFileRepository;
import com.leetcode.learningsystem.service.FileStorageService;
import com.leetcode.learningsystem.service.FileTypeRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final ProblemFileRepository fileRepository;
    private final FileTypeRegistry fileTypeRegistry;
    private Path storageRoot;

    @Value("${app.storage.base-path}")
    private String basePath;

    public FileStorageServiceImpl(ProblemFileRepository fileRepository, FileTypeRegistry fileTypeRegistry) {
        this.fileRepository = fileRepository;
        this.fileTypeRegistry = fileTypeRegistry;
    }

    @PostConstruct
    public void init() {
        storageRoot = Paths.get(basePath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new RuntimeException("Could not create storage directory", e);
        }
    }

    @Override
    public ProblemFile createFile(Long problemId, String fileName, String fileExtension) {
        String sanitizedName = sanitizeFileName(fileName);
        String ext = fileExtension.toLowerCase().replaceAll("[^a-z0-9]", "");
        String fullName = sanitizedName + "." + ext;

        Path problemDir = storageRoot.resolve(String.valueOf(problemId));
        try {
            Files.createDirectories(problemDir);
            Path filePath = problemDir.resolve(fullName);

            // Write default template
            String template = fileTypeRegistry.getDefaultTemplate(ext);
            Files.writeString(filePath, template, StandardCharsets.UTF_8);

            ProblemFile pf = ProblemFile.builder()
                    .problemId(problemId)
                    .fileName(fullName)
                    .fileExtension(ext)
                    .filePath(problemId + "/" + fullName)
                    .fileSize(Files.size(filePath))
                    .build();

            return fileRepository.save(pf);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: " + fullName, e);
        }
    }

    @Override
    public ProblemFile uploadFile(Long problemId, MultipartFile file) {
        String originalName = sanitizeFileName(file.getOriginalFilename());
        String ext = getExtension(originalName);

        Path problemDir = storageRoot.resolve(String.valueOf(problemId));
        try {
            Files.createDirectories(problemDir);
            Path targetPath = problemDir.resolve(originalName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            ProblemFile pf = ProblemFile.builder()
                    .problemId(problemId)
                    .fileName(originalName)
                    .fileExtension(ext)
                    .filePath(problemId + "/" + originalName)
                    .fileSize(file.getSize())
                    .build();

            return fileRepository.save(pf);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + originalName, e);
        }
    }

    @Override
    public Resource loadFile(Long fileId) {
        ProblemFile pf = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found: " + fileId));
        try {
            Path filePath = storageRoot.resolve(pf.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new RuntimeException("File not found on disk: " + pf.getFilePath());
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + fileId, e);
        }
    }

    @Override
    public String readTextContent(Long fileId) {
        ProblemFile pf = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found: " + fileId));

        if (!fileTypeRegistry.isTextBased(pf.getFileExtension())) {
            throw new RuntimeException("File is not text-based: " + pf.getFileName());
        }

        try {
            Path filePath = storageRoot.resolve(pf.getFilePath()).normalize();
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + pf.getFileName(), e);
        }
    }

    @Override
    public void saveTextContent(Long fileId, String content) {
        ProblemFile pf = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found: " + fileId));

        try {
            Path filePath = storageRoot.resolve(pf.getFilePath()).normalize();
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
            pf.setFileSize(Files.size(filePath));
            fileRepository.save(pf);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + pf.getFileName(), e);
        }
    }

    @Override
    public void deleteFile(Long fileId) {
        ProblemFile pf = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found: " + fileId));
        try {
            Path filePath = storageRoot.resolve(pf.getFilePath()).normalize();
            Files.deleteIfExists(filePath);
            fileRepository.delete(pf);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + pf.getFileName(), e);
        }
    }

    @Override
    public void deleteProblemDirectory(Long problemId) {
        Path problemDir = storageRoot.resolve(String.valueOf(problemId));
        try {
            if (Files.exists(problemDir)) {
                try (var stream = Files.walk(problemDir)) {
                    stream.sorted(java.util.Comparator.reverseOrder())
                            .forEach(path -> {
                                try { Files.delete(path); } catch (IOException ignored) {}
                            });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete problem directory: " + problemId, e);
        }
    }

    @Override
    public List<ProblemFile> getFilesForProblem(Long problemId) {
        return fileRepository.findByProblemId(problemId);
    }

    private String sanitizeFileName(String name) {
        if (name == null) return "unnamed";
        // Only allow alphanumeric, dots, hyphens, underscores
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex + 1).toLowerCase() : "txt";
    }
}
