package co.edu.unicauca.lifecycle.dto;

import jakarta.validation.constraints.NotBlank;

public record TransitionRequest(
        @NotBlank(message = "targetState es obligatorio") String targetState,
        String changedBy,
        String comment) {
}
