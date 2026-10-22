package co.edu.unicauca.lifecycle.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/** Dos transiciones simultáneas sobre la misma pregunta: gana la primera (bloqueo optimista con @Version). */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ConcurrencyExceptionHandler {

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> concurrent(OptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiError(LocalDateTime.now(), 409, "Conflict",
                "La pregunta fue modificada por otra operación al mismo tiempo; consulte el estado y reintente",
                List.of()));
    }
}
