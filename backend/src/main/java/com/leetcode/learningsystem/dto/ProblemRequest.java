package com.leetcode.learningsystem.dto;

import com.leetcode.learningsystem.model.Difficulty;
import com.leetcode.learningsystem.model.QuestionType;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemRequest {

    @NotBlank(message = "Problem code is required")
    private String problemCode;

    @NotBlank(message = "Title is required")
    private String title;

    private String leetcodeLink;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    private String solution;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @NotNull(message = "Custom rank is required")
    @Min(value = 1, message = "Rank must be at least 1")
    @Max(value = 10, message = "Rank must be at most 10")
    private Integer customRank;
}
