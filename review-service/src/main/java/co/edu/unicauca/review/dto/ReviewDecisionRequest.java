package co.edu.unicauca.review.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewDecisionRequest(
        @NotBlank(message = "result es obligatorio (APPROVED o REJECTED)") String result,
        String comments) {
}
