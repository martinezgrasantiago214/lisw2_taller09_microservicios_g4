package co.edu.unicauca.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Notification Service (:8086). Solo consume eventos de RabbitMQ (asíncrono puro)
 * y simula el envío de correos sin bloquear el flujo principal. Expone GET
 * /notifications para verificar desde Postman que el evento fue procesado.
 */
@SpringBootApplication
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
