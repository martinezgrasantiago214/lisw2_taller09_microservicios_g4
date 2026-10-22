package co.edu.unicauca.question;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Question Service (:8083). Administra el contenido de las preguntas y coordina el flujo:
 * - REST síncrono hacia User Service (autenticación) y Validation Service (validación).
 * - Publica en RabbitMQ el evento QuestionCreated y el comando SubmitQuestionForReview.
 * - Consume OperationResult y QuestionStateChanged del Lifecycle Service.
 */
@SpringBootApplication
public class QuestionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuestionServiceApplication.class, args);
    }
}
