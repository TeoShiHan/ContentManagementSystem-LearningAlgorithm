package com.leetcode.learningsystem.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemFile {
    private String fileName;
    private String fileExtension;
    private String absolutePath;
    private Long fileSize;
    private String openWith;
}
