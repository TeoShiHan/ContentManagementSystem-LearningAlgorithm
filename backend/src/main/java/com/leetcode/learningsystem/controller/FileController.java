package com.leetcode.learningsystem.controller;

import com.leetcode.learningsystem.dto.CreateFileRequest;
import com.leetcode.learningsystem.dto.ProblemFileResponse;
import com.leetcode.learningsystem.service.FileStorageService;
import com.leetcode.learningsystem.service.FileTypeRegistry;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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

    @PostMapping("/problem/{folderId}/create")
    public ResponseEntity<ProblemFileResponse> createFile(@PathVariable String folderId,
                                                          @Valid @RequestBody CreateFileRequest request) {
        ProblemFileResponse pf = fileStorageService.createFile(folderId, request.getFileName(), request.getFileExtension());
        return ResponseEntity.status(HttpStatus.CREATED).body(pf);
    }

    @PostMapping("/problem/{folderId}/upload")
    public ResponseEntity<ProblemFileResponse> uploadFile(@PathVariable String folderId,
                                                          @RequestParam("file") MultipartFile file) {
        ProblemFileResponse pf = fileStorageService.uploadFile(folderId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(pf);
    }

    @GetMapping("/problem/{folderId}/{fileName}/content")
    public ResponseEntity<Map<String, String>> getFileContent(@PathVariable String folderId,
                                                               @PathVariable String fileName) {
        String content = fileStorageService.readTextContent(folderId, fileName);
        return ResponseEntity.ok(Map.of("content", content));
    }

    @PutMapping("/problem/{folderId}/{fileName}/content")
    public ResponseEntity<Void> saveFileContent(@PathVariable String folderId,
                                                 @PathVariable String fileName,
                                                 @RequestBody Map<String, String> body) {
        fileStorageService.saveTextContent(folderId, fileName, body.get("content"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/problem/{folderId}/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String folderId,
                                            @PathVariable String fileName) {
        fileStorageService.deleteFile(folderId, fileName);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/problem/{folderId}/{fileName}/open")
    public ResponseEntity<Void> openFileLocally(@PathVariable String folderId,
                                                 @PathVariable String fileName) {
        fileStorageService.openFileLocally(folderId, fileName);
        return ResponseEntity.ok().build();
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
}
