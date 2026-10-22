package co.edu.unicauca.validation.rules;

import co.edu.unicauca.validation.dto.ValidationRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)
public class StatementRule implements ValidationRule {

    static final int MIN_LENGTH = 20;
    static final int MAX_LENGTH = 1000;

    @Override public String id() { return "R1"; }
    @Override public String name() { return "Enunciado"; }
    @Override public String description() {
        return "El enunciado es obligatorio y debe tener entre " + MIN_LENGTH + " y " + MAX_LENGTH + " caracteres.";
    }

    @Override
    public List<String> validate(ValidationRequestDTO q) {
        if (q.text() == null || q.text().isBlank()) {
            return List.of("El enunciado de la pregunta es obligatorio.");
        }
        int length = q.text().trim().length();
        if (length < MIN_LENGTH) {
            return List.of("El enunciado debe tener al menos " + MIN_LENGTH + " caracteres (tiene " + length + ").");
        }
        if (length > MAX_LENGTH) {
            return List.of("El enunciado no puede superar " + MAX_LENGTH + " caracteres (tiene " + length + ").");
        }
        return List.of();
    }
}
