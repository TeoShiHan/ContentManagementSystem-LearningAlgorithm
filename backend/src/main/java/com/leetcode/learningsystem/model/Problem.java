package com.leetcode.learningsystem.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    private String problemCode;
    private String title;
    private String leetcodeLink;
    private QuestionType questionType;
    private String solution;
    private Difficulty difficulty;
    private Integer customRank;
}
