package com.leetcode.learningsystem.service.impl;

import com.leetcode.learningsystem.dto.ProblemFileResponse;
import com.leetcode.learningsystem.service.FileStorageService;
import com.leetcode.learningsystem.service.FileTypeRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileTypeRegistry fileTypeRegistry;
    private volatile Path storageRoot;

    @Value("${app.storage.base-path}")
    private String basePath;

    public FileStorageServiceImpl(FileTypeRegistry fileTypeRegistry) {
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
    public Path getStorageRoot() {
        return storageRoot;
    }

    @Override
    public ProblemFileResponse createFile(String folderId, String fileName, String fileExtension) {
        String sanitizedName = sanitizeFileName(fileName);
        String ext = fileExtension.toLowerCase().replaceAll("[^a-z0-9]", "");
        String fullName = sanitizedName + "." + ext;

        Path problemDir = storageRoot.resolve(folderId);
        try {
            Files.createDirectories(problemDir);
            Path filePath = problemDir.resolve(fullName);

            String template = fileTypeRegistry.getDefaultTemplate(ext);
            Files.writeString(filePath, template, StandardCharsets.UTF_8);

            return toResponse(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: " + fullName, e);
        }
    }

    @Override
    public ProblemFileResponse uploadFile(String folderId, MultipartFile file) {
        String originalName = sanitizeFileName(file.getOriginalFilename());

        Path problemDir = storageRoot.resolve(folderId);
        try {
            Files.createDirectories(problemDir);
            Path targetPath = problemDir.resolve(originalName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return toResponse(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + originalName, e);
        }
    }

    @Override
    public String readTextContent(String folderId, String fileName) {
        Path filePath = storageRoot.resolve(folderId).resolve(fileName).normalize();
        validatePathSafety(filePath);
        String ext = getExtension(fileName);

        if (!fileTypeRegistry.isTextBased(ext)) {
            throw new RuntimeException("File is not text-based: " + fileName);
        }

        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + fileName, e);
        }
    }

    @Override
    public void saveTextContent(String folderId, String fileName, String content) {
        Path filePath = storageRoot.resolve(folderId).resolve(fileName).normalize();
        validatePathSafety(filePath);
        try {
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + fileName, e);
        }
    }

    @Override
    public void deleteFile(String folderId, String fileName) {
        Path filePath = storageRoot.resolve(folderId).resolve(fileName).normalize();
        validatePathSafety(filePath);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + fileName, e);
        }
    }

    @Override
    public void deleteProblemDirectory(String folderId) {
        Path problemDir = storageRoot.resolve(folderId);
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
            throw new RuntimeException("Failed to delete problem directory: " + folderId, e);
        }
    }

    @Override
    public void openFileLocally(String folderId, String fileName) {
        Path filePath = storageRoot.resolve(folderId).resolve(fileName).normalize();
        validatePathSafety(filePath);
        String absPath = filePath.toAbsolutePath().toString();
        String ext = getExtension(fileName);

        try {
            ProcessBuilder pb;
            if (fileTypeRegistry.isTextBased(ext)
                    && !"excalidraw".equals(ext)
                    && !"drawio".equals(ext)) {
                // Open text/code files in VS Code
                pb = new ProcessBuilder("code", absPath);
            } else {
                // Open with system default (excalidraw desktop, drawio, images, etc.)
                pb = new ProcessBuilder("cmd", "/c", "start", "", absPath);
            }
            pb.redirectErrorStream(true);
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file locally: " + absPath, e);
        }
    }

    @Override
    public String getStorageBasePath() {
        return storageRoot.toString();
    }

    @Override
    public void setStorageBasePath(String newPath) {
        Path newRoot = Paths.get(newPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(newRoot);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create/access directory: " + newPath, e);
        }
        this.storageRoot = newRoot;
        this.basePath = newPath;
    }

    public ProblemFileResponse toResponse(Path filePath) {
        String name = filePath.getFileName().toString();
        String ext = getExtension(name);
        try {
            return ProblemFileResponse.builder()
                    .id(name)
                    .fileName(name)
                    .fileExtension(ext)
                    .filePath(filePath.toAbsolutePath().toString())
                    .fileSize(Files.size(filePath))
                    .openWith(fileTypeRegistry.getOpenWith(ext))
                    .build();
        } catch (IOException e) {
            return ProblemFileResponse.builder()
                    .id(name)
                    .fileName(name)
                    .fileExtension(ext)
                    .filePath(filePath.toAbsolutePath().toString())
                    .fileSize(0L)
                    .openWith(fileTypeRegistry.getOpenWith(ext))
                    .build();
        }
    }

    private void validatePathSafety(Path resolved) {
        if (!resolved.startsWith(storageRoot)) {
            throw new RuntimeException("Path traversal detected");
        }
    }

    private String sanitizeFileName(String name) {
        if (name == null) return "unnamed";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(dotIndex + 1).toLowerCase() : "txt";
    }
}
