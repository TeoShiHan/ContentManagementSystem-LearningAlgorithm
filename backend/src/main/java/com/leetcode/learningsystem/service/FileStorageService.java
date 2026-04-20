package com.leetcode.learningsystem.service;

import com.leetcode.learningsystem.model.ProblemFile;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileStorageService {
    ProblemFile createFile(Long problemId, String fileName, String fileExtension);
    ProblemFile uploadFile(Long problemId, MultipartFile file);
    Resource loadFile(Long fileId);
    String readTextContent(Long fileId);
    void saveTextContent(Long fileId, String content);
    void deleteFile(Long fileId);
    void deleteProblemDirectory(Long problemId);
    List<ProblemFile> getFilesForProblem(Long problemId);
}
