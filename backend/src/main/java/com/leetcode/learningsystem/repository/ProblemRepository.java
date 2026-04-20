package com.leetcode.learningsystem.repository;

import com.leetcode.learningsystem.model.Difficulty;
import com.leetcode.learningsystem.model.Problem;
import com.leetcode.learningsystem.model.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    List<Problem> findByDifficulty(Difficulty difficulty);

    List<Problem> findByQuestionType(QuestionType questionType);

    List<Problem> findByDifficultyAndQuestionType(Difficulty difficulty, QuestionType questionType);

    List<Problem> findByCustomRankGreaterThanEqual(Integer rank);

    @Query("SELECT p FROM Problem p WHERE " +
           "(:difficulty IS NULL OR p.difficulty = :difficulty) AND " +
           "(:questionType IS NULL OR p.questionType = :questionType) AND " +
           "(:minRank IS NULL OR p.customRank >= :minRank) AND " +
           "(:maxRank IS NULL OR p.customRank <= :maxRank) AND " +
           "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.problemCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Problem> findByFilters(
            @Param("difficulty") Difficulty difficulty,
            @Param("questionType") QuestionType questionType,
            @Param("minRank") Integer minRank,
            @Param("maxRank") Integer maxRank,
            @Param("search") String search
    );

    long countByDifficulty(Difficulty difficulty);

    long countByQuestionType(QuestionType questionType);
}
