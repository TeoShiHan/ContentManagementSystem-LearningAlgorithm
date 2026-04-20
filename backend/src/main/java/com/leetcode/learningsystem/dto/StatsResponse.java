package com.leetcode.learningsystem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatsResponse {
    private long totalProblems;
    private long easyCount;
    private long mediumCount;
    private long hardCount;
    private java.util.Map<String, Long> byQuestionType;
    private double averageCustomRank;
}
