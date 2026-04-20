package com.leetcode.learningsystem.controller;

import com.leetcode.learningsystem.dto.CreateFileRequest;
import com.leetcode.learningsystem.dto.ProblemFileResponse;
import com.leetcode.learningsystem.model.ProblemFile;
import com.leetcode.learningsystem.service.FileStorageService;
import com.leetcode.learningsystem.service.FileTypeRegistry;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class FileController {

    private final FileStorageService fileStorageService;
    private final FileTypeRegistry fileTypeRegistry;

    public FileController(FileStorageService fileStorageService, FileTypeRegistry fileTypeRegistry) {
        this.fileStorageService = fileStorageService;
        this.fileTypeRegistry = fileTypeRegistry;
    }

    @PostMapping("/problem/{problemId}/create")
    public ResponseEntity<ProblemFileResponse> createFile(@PathVariable Long problemId,
                                                          @Valid @RequestBody CreateFileRequest request) {
        ProblemFile pf = fileStorageService.createFile(problemId, request.getFileName(), request.getFileExtension());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pf));
    }

    @PostMapping("/problem/{problemId}/upload")
    public ResponseEntity<ProblemFileResponse> uploadFile(@PathVariable Long problemId,
                                                          @RequestParam("file") MultipartFile file) {
        ProblemFile pf = fileStorageService.uploadFile(problemId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pf));
    }

    @GetMapping("/problem/{problemId}")
    public ResponseEntity<List<ProblemFileResponse>> getFiles(@PathVariable Long problemId) {
        List<ProblemFileResponse> files = fileStorageService.getFilesForProblem(problemId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        Resource resource = fileStorageService.loadFile(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/{fileId}/content")
    public ResponseEntity<Map<String, String>> getFileContent(@PathVariable Long fileId) {
        String content = fileStorageService.readTextContent(fileId);
        return ResponseEntity.ok(Map.of("content", content));
    }

    @PutMapping("/{fileId}/content")
    public ResponseEntity<Void> saveFileContent(@PathVariable Long fileId,
                                                 @RequestBody Map<String, String> body) {
        fileStorageService.saveTextContent(fileId, body.get("content"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        fileStorageService.deleteFile(fileId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{fileId}/open-local")
    public ResponseEntity<Void> openFileLocally(@PathVariable Long fileId) {
        fileStorageService.openFileLocally(fileId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{fileId}/absolute-path")
    public ResponseEntity<Map<String, String>> getAbsolutePath(@PathVariable Long fileId) {
        String path = fileStorageService.getAbsolutePath(fileId);
        return ResponseEntity.ok(Map.of("path", path));
    }

    @GetMapping("/config/storage-path")
    public ResponseEntity<Map<String, String>> getStoragePath() {
        return ResponseEntity.ok(Map.of("path", fileStorageService.getStorageBasePath()));
    }

    @PostMapping("/config/storage-path")
    public ResponseEntity<Map<String, String>> setStoragePath(@RequestBody Map<String, String> body) {
        String newPath = body.get("path");
        if (newPath == null || newPath.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Path must not be empty"));
        }
        fileStorageService.setStorageBasePath(newPath.trim());
        return ResponseEntity.ok(Map.of("path", fileStorageService.getStorageBasePath()));
    }

    @GetMapping("/supported-types")
    public ResponseEntity<Map<String, String>> getSupportedTypes() {
        return ResponseEntity.ok(fileTypeRegistry.getSupportedTypes());
    }

    private ProblemFileResponse toResponse(ProblemFile pf) {
        return ProblemFileResponse.builder()
                .id(pf.getId())
                .fileName(pf.getFileName())
                .fileExtension(pf.getFileExtension())
                .filePath(pf.getFilePath())
                .fileSize(pf.getFileSize())
                .createdAt(pf.getCreatedAt())
                .openWith(fileTypeRegistry.getOpenWith(pf.getFileExtension()))
                .build();
    }
}
