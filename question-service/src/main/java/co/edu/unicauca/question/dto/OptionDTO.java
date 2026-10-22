package co.edu.unicauca.question.dto;

import jakarta.validation.constraints.NotBlank;

public record OptionDTO(@NotBlank String id, @NotBlank String text) {
}
