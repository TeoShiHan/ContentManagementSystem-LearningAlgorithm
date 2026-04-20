package com.leetcode.learningsystem.service;

import com.leetcode.learningsystem.dto.*;
import com.leetcode.learningsystem.model.Difficulty;
import com.leetcode.learningsystem.model.QuestionType;

import java.util.List;

public interface ProblemService {
    ProblemResponse createProblem(ProblemRequest request);
    ProblemResponse updateProblem(String id, ProblemRequest request);
    ProblemResponse getProblem(String id);
    List<ProblemResponse> getAllProblems();
    List<ProblemResponse> queryProblems(Difficulty difficulty, QuestionType questionType,
                                         Integer minRank, Integer maxRank, String search);
    void deleteProblem(String id);
    StatsResponse getStats();
}
