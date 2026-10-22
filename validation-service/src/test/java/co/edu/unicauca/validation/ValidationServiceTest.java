package co.edu.unicauca.validation;

import co.edu.unicauca.validation.dto.OptionDTO;
import co.edu.unicauca.validation.dto.ValidationRequestDTO;
import co.edu.unicauca.validation.dto.ValidationResultDTO;
import co.edu.unicauca.validation.rules.*;
import co.edu.unicauca.validation.service.ValidationService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private final ValidationService service = new ValidationService(List.of(
            new StatementRule(), new OptionsRule(), new CorrectAnswerRule(), new ForbiddenOptionsRule()));

    private static List<OptionDTO> options(String a, String b, String c, String d) {
        return List.of(new OptionDTO("A", a), new OptionDTO("B", b), new OptionDTO("C", c), new OptionDTO("D", d));
    }

    @Test
    void preguntaValidaNoTieneErrores() {
        ValidationResultDTO result = service.validate(new ValidationRequestDTO(
                "¿Qué patrón de diseño desacopla una abstracción de su implementación?",
                options("Bridge", "Singleton", "Observer", "Builder"), "A"));
        assertTrue(result.valid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void preguntaInvalidaAcumulaTodosLosErrores() {
        ValidationResultDTO result = service.validate(new ValidationRequestDTO(
                "Muy corta", List.of(new OptionDTO("A", "Uno"), new OptionDTO("A", "Uno")), "Z"));
        assertFalse(result.valid());
        assertEquals(5, result.errors().size(), result.errors().toString());
    }

    @Test
    void rechazaTodasLasAnteriores() {
        ValidationResultDTO result = service.validate(new ValidationRequestDTO(
                "¿Cuál de los siguientes es un principio SOLID aplicable al diseño?",
                options("SRP", "OCP", "LSP", "Todas las anteriores"), "D"));
        assertFalse(result.valid());
        assertEquals(1, result.errors().size());
    }
}
