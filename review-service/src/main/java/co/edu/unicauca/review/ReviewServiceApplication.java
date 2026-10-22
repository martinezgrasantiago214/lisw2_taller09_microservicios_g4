package co.edu.unicauca.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Review Service (:8085). Registra asignaciones de revisores y resultados de revisión.
 * - REST síncrono hacia User, Question y Lifecycle Service.
 * - Publica ReviewerAssigned y QuestionReviewed en RabbitMQ.
 * - @EnableScheduling: reconciliación periódica de asignaciones PENDING.
 */
@SpringBootApplication
@EnableScheduling
public class ReviewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApplication.class, args);
    }
}
