package co.edu.unicauca.notification.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableException extends ApiException {
    public UnprocessableException(String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
