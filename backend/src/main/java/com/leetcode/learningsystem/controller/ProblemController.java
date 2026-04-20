package com.leetcode.learningsystem.controller;

import com.leetcode.learningsystem.dto.*;
import com.leetcode.learningsystem.model.Difficulty;
import com.leetcode.learningsystem.model.QuestionType;
import com.leetcode.learningsystem.service.ProblemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(@Valid @RequestBody ProblemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(problemService.createProblem(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemResponse> updateProblem(@PathVariable Long id,
                                                          @Valid @RequestBody ProblemRequest request) {
        return ResponseEntity.ok(problemService.updateProblem(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemResponse> getProblem(@PathVariable Long id) {
        return ResponseEntity.ok(problemService.getProblem(id));
    }

    @GetMapping
    public ResponseEntity<List<ProblemResponse>> getAllProblems() {
        return ResponseEntity.ok(problemService.getAllProblems());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProblemResponse>> queryProblems(
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) QuestionType questionType,
            @RequestParam(required = false) Integer minRank,
            @RequestParam(required = false) Integer maxRank,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(problemService.queryProblems(difficulty, questionType, minRank, maxRank, search));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(problemService.getStats());
    }
}
