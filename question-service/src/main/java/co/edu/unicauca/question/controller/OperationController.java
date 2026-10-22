package co.edu.unicauca.question.controller;

import co.edu.unicauca.question.dto.OperationDTO;
import co.edu.unicauca.question.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Consulta posterior del resultado de una operación asíncrona (PROCESSING | COMPLETED | FAILED). */
@RestController
@RequestMapping("/operations")
public class OperationController {

    private final QuestionService questionService;

    public OperationController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{operationId}")
    public ResponseEntity<OperationDTO> find(@PathVariable String operationId) {
        return ResponseEntity.ok(questionService.findOperation(operationId));
    }
}
