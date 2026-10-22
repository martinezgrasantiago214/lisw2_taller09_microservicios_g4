package co.edu.unicauca.review.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> api(ApiException ex) {
        return build(ex.getStatus(), ex.getMessage(), ex.getErrors());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
        return build(HttpStatus.BAD_REQUEST, "Petición inválida", errors);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> unreadable(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "El cuerpo o los parámetros de la petición no tienen el formato esperado",
                List.of());
    }

    /** RabbitMQ caído o inalcanzable al publicar: no se informa éxito falso al cliente. */
    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<ApiError> broker(AmqpException ex) {
        log.error("Error de mensajería: {}", ex.getMessage());
        return build(HttpStatus.SERVICE_UNAVAILABLE,
                "El broker de mensajería no está disponible; la operación no se realizó", List.of());
    }

    /**
     * Cualquier error no previsto responde con el mismo formato ApiError (500) y queda
     * en el log, en lugar de la página de error por defecto de Spring.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> unexpected(RuntimeException ex) {
        log.error("Error no controlado", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servicio; la operación no se completó", List.of());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, List<String> errors) {
        return ResponseEntity.status(status)
                .body(new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message, errors));
    }
}
