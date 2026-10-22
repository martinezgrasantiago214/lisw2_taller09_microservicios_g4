package co.edu.unicauca.validation.rules;

import co.edu.unicauca.validation.dto.OptionDTO;
import co.edu.unicauca.validation.dto.ValidationRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

/** Saber Pro: selección múltiple con única respuesta, 4 opciones (A-D) distintas y no vacías. */
@Component
@Order(2)
public class OptionsRule implements ValidationRule {

    static final int REQUIRED_OPTIONS = 4;

    @Override public String id() { return "R2"; }
    @Override public String name() { return "Opciones de respuesta"; }
    @Override public String description() {
        return "Debe haber exactamente " + REQUIRED_OPTIONS + " opciones con identificador y texto, sin repetir.";
    }

    @Override
    public List<String> validate(ValidationRequestDTO q) {
        List<String> errors = new ArrayList<>();
        List<OptionDTO> options = q.options() == null ? List.of() : q.options();
        if (options.size() != REQUIRED_OPTIONS) {
            errors.add("La pregunta debe tener exactamente " + REQUIRED_OPTIONS + " opciones (tiene " + options.size() + ").");
        }
        Set<String> ids = new HashSet<>();
        Set<String> texts = new HashSet<>();
        for (int i = 0; i < options.size(); i++) {
            OptionDTO option = options.get(i);
            String label = "Opción " + (i + 1);
            if (option == null) {
                errors.add(label + " está vacía.");
                continue;
            }
            if (option.id() == null || option.id().isBlank()) {
                errors.add(label + " no tiene identificador.");
            } else if (!ids.add(option.id().trim().toUpperCase(Locale.ROOT))) {
                errors.add("El identificador de opción '" + option.id() + "' está repetido.");
            }
            if (option.text() == null || option.text().isBlank()) {
                errors.add(label + " no tiene texto.");
            } else if (!texts.add(option.text().trim().toLowerCase(Locale.ROOT))) {
                errors.add("El texto de la opción '" + option.text().trim() + "' está repetido.");
            }
        }
        return errors;
    }
}
