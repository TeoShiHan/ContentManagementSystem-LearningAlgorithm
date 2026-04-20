package com.leetcode.learningsystem.service;

import com.leetcode.learningsystem.dto.ProblemFileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {
    ProblemFileResponse createFile(String folderId, String fileName, String fileExtension);
    ProblemFileResponse uploadFile(String folderId, MultipartFile file);
    String readTextContent(String folderId, String fileName);
    void saveTextContent(String folderId, String fileName, String content);
    void deleteFile(String folderId, String fileName);
    void deleteProblemDirectory(String folderId);
    void openFileLocally(String folderId, String fileName);
    String getStorageBasePath();
    void setStorageBasePath(String newPath);
    Path getStorageRoot();
}
