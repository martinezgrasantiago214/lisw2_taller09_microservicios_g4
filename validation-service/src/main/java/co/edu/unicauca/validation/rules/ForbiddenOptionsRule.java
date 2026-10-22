package co.edu.unicauca.validation.rules;

import co.edu.unicauca.validation.dto.OptionDTO;
import co.edu.unicauca.validation.dto.ValidationRequestDTO;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Lineamiento de construcción de ítems: no usar "todas/ninguna de las anteriores". */
@Component
@Order(4)
public class ForbiddenOptionsRule implements ValidationRule {

    private static final List<String> FORBIDDEN = List.of(
            "todas las anteriores", "ninguna de las anteriores", "todas las opciones", "ninguna de las opciones");

    @Override public String id() { return "R4"; }
    @Override public String name() { return "Opciones prohibidas"; }
    @Override public String description() {
        return "Las opciones no pueden ser 'todas/ninguna de las anteriores' (única respuesta independiente).";
    }

    @Override
    public List<String> validate(ValidationRequestDTO q) {
        List<OptionDTO> options = q.options() == null ? List.of() : q.options();
        return options.stream()
                .filter(Objects::nonNull)
                .filter(o -> o.text() != null && FORBIDDEN.stream().anyMatch(f -> normalize(o.text()).contains(f)))
                .map(o -> "La opción '" + o.id() + "' usa una formulación no permitida: '" + o.text().trim() + "'.")
                .toList();
    }

    private static String normalize(String text) {
        String noAccents = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return noAccents.toLowerCase(Locale.ROOT).trim();
    }
}
