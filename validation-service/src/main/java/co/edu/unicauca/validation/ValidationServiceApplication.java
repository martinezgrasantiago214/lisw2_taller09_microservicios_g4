package co.edu.unicauca.validation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Validation Service (:8082). Valida la estructura, las opciones y la respuesta
 * correcta de una pregunta. Es un servicio sin estado invocado síncronamente
 * por el Question Service (POST /validate).
 */
@SpringBootApplication
public class ValidationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ValidationServiceApplication.class, args);
    }
}
