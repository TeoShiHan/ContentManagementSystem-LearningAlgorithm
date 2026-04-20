package com.leetcode.learningsystem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemFileResponse {
    private String id;          // file name (acts as identifier)
    private String fileName;
    private String fileExtension;
    private String filePath;    // absolute path on disk
    private Long fileSize;
    private String openWith;    // hint: "vscode", "web-excalidraw", "web-drawio", "browser"
}
