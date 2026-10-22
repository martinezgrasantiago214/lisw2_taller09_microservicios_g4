package co.edu.unicauca.validation.dto;

import java.util.List;

/** Contenido de la pregunta a validar. No usa @NotNull: las reglas reportan todos los errores juntos. */
public record ValidationRequestDTO(String text, List<OptionDTO> options, String correctOptionId) {
}
