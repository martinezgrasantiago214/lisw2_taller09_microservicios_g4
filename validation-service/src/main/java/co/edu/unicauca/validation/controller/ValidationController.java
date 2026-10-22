package co.edu.unicauca.validation.controller;

import co.edu.unicauca.validation.dto.RuleInfoDTO;
import co.edu.unicauca.validation.dto.ValidationRequestDTO;
import co.edu.unicauca.validation.dto.ValidationResultDTO;
import co.edu.unicauca.validation.service.ValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/validate")
public class ValidationController {

    private final ValidationService validationService;

    public ValidationController(ValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Siempre responde 200 OK: el resultado (valid + errors) es el dato que el
     * Question Service necesita para decidir entre 201 Created y 400 Bad Request.
     */
    @PostMapping
    public ResponseEntity<ValidationResultDTO> validate(@RequestBody ValidationRequestDTO question) {
        return ResponseEntity.ok(validationService.validate(question));
    }

    @GetMapping("/rules")
    public ResponseEntity<List<RuleInfoDTO>> rules() {
        return ResponseEntity.ok(validationService.rules());
    }
}
