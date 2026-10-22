package co.edu.unicauca.review.dto;

import jakarta.validation.constraints.NotNull;

public record AssignReviewerRequest(
        @NotNull(message = "questionId es obligatorio") Long questionId,
        @NotNull(message = "reviewerId es obligatorio") Long reviewerId) {
}
