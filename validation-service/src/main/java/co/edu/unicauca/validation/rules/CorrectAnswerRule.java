package co.edu.unicauca.validation.rules;

import co.edu.unicauca.validation.dto.OptionDTO;
import co.edu.unicauca.validation.dto.ValidationRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Order(3)
public class CorrectAnswerRule implements ValidationRule {

    @Override public String id() { return "R3"; }
    @Override public String name() { return "Respuesta correcta"; }
    @Override public String description() {
        return "La clave (correctOptionId) es obligatoria y debe corresponder a una de las opciones.";
    }

    @Override
    public List<String> validate(ValidationRequestDTO q) {
        if (q.correctOptionId() == null || q.correctOptionId().isBlank()) {
            return List.of("Debe indicar la opción correcta (correctOptionId).");
        }
        List<OptionDTO> options = q.options() == null ? List.of() : q.options();
        boolean exists = options.stream().filter(Objects::nonNull)
                .anyMatch(o -> q.correctOptionId().trim().equalsIgnoreCase(o.id() == null ? "" : o.id().trim()));
        return exists ? List.of()
                : List.of("La respuesta correcta '" + q.correctOptionId() + "' no corresponde a ninguna opción.");
    }
}
