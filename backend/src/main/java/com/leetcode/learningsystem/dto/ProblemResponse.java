package com.leetcode.learningsystem.dto;

import com.leetcode.learningsystem.model.Difficulty;
import com.leetcode.learningsystem.model.QuestionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemResponse {
    private String id;          // folder name
    private String problemCode;
    private String title;
    private String leetcodeLink;
    private QuestionType questionType;
    private String solution;
    private Difficulty difficulty;
    private Integer customRank;
    private List<ProblemFileResponse> files;
}
