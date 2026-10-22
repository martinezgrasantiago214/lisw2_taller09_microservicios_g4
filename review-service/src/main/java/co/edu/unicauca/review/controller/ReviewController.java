package co.edu.unicauca.review.controller;

import co.edu.unicauca.review.dto.*;
import co.edu.unicauca.review.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** 201 | 401 | 403 | 409 (pregunta no elegible) | 422 (revisor no elegible) | 503. */
    @PostMapping("/assign")
    public ResponseEntity<AssignmentDTO> assign(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody AssignReviewerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.assignReviewer(authorization, request));
    }

    @PostMapping("/assignments/{assignmentId}/decision")
    public ResponseEntity<ReviewDTO> decide(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable Long assignmentId, @Valid @RequestBody ReviewDecisionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.decide(authorization, assignmentId, request));
    }

    @GetMapping("/assignments")
    public ResponseEntity<List<AssignmentDTO>> assignments(@RequestParam(required = false) Long questionId) {
        return ResponseEntity.ok(reviewService.assignments(questionId));
    }

    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<AssignmentDTO> assignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(reviewService.assignment(assignmentId));
    }

    @GetMapping
    public ResponseEntity<List<ReviewDTO>> reviews(@RequestParam(required = false) Long questionId) {
        return ResponseEntity.ok(reviewService.reviews(questionId));
    }
}
