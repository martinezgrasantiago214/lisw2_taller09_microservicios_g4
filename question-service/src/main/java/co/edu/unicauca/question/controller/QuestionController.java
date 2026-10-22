package co.edu.unicauca.question.controller;

import co.edu.unicauca.question.dto.*;
import co.edu.unicauca.question.service.QuestionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    /** 201 Created (DRAFT) | 400 Bad Request (validación) | 401 | 403 | 503. */
    @PostMapping
    public ResponseEntity<CreateQuestionResponse> create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody CreateQuestionRequest request) {
        CreateQuestionResponse response = questionService.create(authorization, request);
        return ResponseEntity.created(URI.create("/questions/" + response.id())).body(response);
    }

    /** 202 Accepted con operationId: la transición se procesa de forma asíncrona. */
    @PostMapping("/{questionId}/submit")
    public ResponseEntity<SubmitAcceptedResponse> submit(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable Long questionId) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(questionService.submitForReview(authorization, questionId));
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> update(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable Long questionId, @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.ok(questionService.update(authorization, questionId, request));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> findById(@PathVariable Long questionId) {
        return ResponseEntity.ok(questionService.findById(questionId));
    }

    @GetMapping
    public ResponseEntity<List<QuestionDTO>> findAll(@RequestParam(required = false) Long authorId) {
        return ResponseEntity.ok(questionService.findAll(authorId));
    }
}
