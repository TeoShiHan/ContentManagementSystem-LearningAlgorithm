package com.leetcode.learningsystem.repository;

import com.leetcode.learningsystem.model.ProblemFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemFileRepository extends JpaRepository<ProblemFile, Long> {
    List<ProblemFile> findByProblemId(Long problemId);
    void deleteByProblemId(Long problemId);
}
