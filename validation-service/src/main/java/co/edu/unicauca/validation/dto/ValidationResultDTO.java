package co.edu.unicauca.validation.dto;

import java.util.List;

public record ValidationResultDTO(boolean valid, List<String> errors) {

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
