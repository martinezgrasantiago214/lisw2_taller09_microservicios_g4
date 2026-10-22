package co.edu.unicauca.validation.rules;

import co.edu.unicauca.validation.dto.ValidationRequestDTO;

import java.util.List;

/**
 * Regla de validación (patrón Strategy). Cada regla es un @Component; el
 * ValidationService aplica todas las reglas activas y acumula los errores.
 */
public interface ValidationRule {

    String id();

    String name();

    String description();

    default boolean active() {
        return true;
    }

    /** @return lista de errores encontrados (vacía si la pregunta cumple la regla). */
    List<String> validate(ValidationRequestDTO question);
}
