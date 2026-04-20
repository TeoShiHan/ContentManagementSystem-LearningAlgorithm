package com.leetcode.learningsystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFileRequest {
    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "File extension is required")
    private String fileExtension; // "txt", "cpp", "excalidraw", "drawio", "py", "java", "md"
}
