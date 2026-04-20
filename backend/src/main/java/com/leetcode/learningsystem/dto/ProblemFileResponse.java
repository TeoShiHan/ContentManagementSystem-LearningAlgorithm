package com.leetcode.learningsystem.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemFileResponse {
    private Long id;
    private String fileName;
    private String fileExtension;
    private String filePath;
    private Long fileSize;
    private LocalDateTime createdAt;
    private String openWith; // hint for frontend: "vscode", "web-excalidraw", "web-drawio", "browser"
}
