package co.edu.unicauca.lifecycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lifecycle Service (:8084). Autoridad única sobre el estado de la pregunta.
 * - Consume (RabbitMQ) QuestionCreated y el comando SubmitQuestionForReview.
 * - Expone REST para consultar el estado y solicitar transiciones síncronas.
 * - Publica OperationResult, QuestionStateChanged y QuestionSubmittedForReview.
 */
@SpringBootApplication
public class LifecycleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifecycleServiceApplication.class, args);
    }
}
