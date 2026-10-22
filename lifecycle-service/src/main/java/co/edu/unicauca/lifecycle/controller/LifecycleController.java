package co.edu.unicauca.lifecycle.controller;

import co.edu.unicauca.lifecycle.dto.OperationResultDTO;
import co.edu.unicauca.lifecycle.dto.QuestionStateDTO;
import co.edu.unicauca.lifecycle.dto.StateHistoryDTO;
import co.edu.unicauca.lifecycle.dto.TransitionRequest;
import co.edu.unicauca.lifecycle.service.LifecycleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lifecycle")
public class LifecycleController {

    private final LifecycleService lifecycleService;

    public LifecycleController(LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionStateDTO> getState(@PathVariable Long questionId) {
        return ResponseEntity.ok(lifecycleService.getState(questionId));
    }

    @GetMapping("/{questionId}/history")
    public ResponseEntity<List<StateHistoryDTO>> history(@PathVariable Long questionId) {
        return ResponseEntity.ok(lifecycleService.history(questionId));
    }

    /** 200 OK | 400 (estado desconocido / falta motivo) | 404 | 409 (transición no permitida). */
    @PostMapping("/{questionId}/transition")
    public ResponseEntity<QuestionStateDTO> transition(@PathVariable Long questionId,
                                                       @Valid @RequestBody TransitionRequest request) {
        return ResponseEntity.ok(lifecycleService.requestTransition(questionId, request));
    }

    @GetMapping("/operations/{operationId}")
    public ResponseEntity<OperationResultDTO> operation(@PathVariable String operationId) {
        return ResponseEntity.ok(lifecycleService.getOperation(operationId));
    }
}
