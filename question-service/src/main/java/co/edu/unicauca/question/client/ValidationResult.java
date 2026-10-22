package co.edu.unicauca.question.client;

import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {
}
