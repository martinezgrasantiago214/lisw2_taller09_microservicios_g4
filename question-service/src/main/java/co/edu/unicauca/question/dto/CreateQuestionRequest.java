package co.edu.unicauca.question.dto;

import java.util.List;

/**
 * Contenido de la pregunta. Las reglas de negocio (4 opciones, clave válida, etc.)
 * las evalúa el Validation Service, por eso aquí no hay anotaciones de validación.
 */
public record CreateQuestionRequest(String text, List<OptionDTO> options, String correctOptionId) {
}
