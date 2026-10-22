package co.edu.unicauca.review.dto;

import co.edu.unicauca.review.model.Review;

import java.time.LocalDateTime;

public record ReviewDTO(Long id, Long assignmentId, Long questionId, Long reviewerId, String result, String comments,
                        String questionState, LocalDateTime createdAt) {

    public static ReviewDTO from(Review r, String questionState) {
        return new ReviewDTO(r.getId(), r.getAssignmentId(), r.getQuestionId(), r.getReviewerId(), r.getResult().name(),
                r.getComments(), questionState, r.getCreatedAt());
    }
}
