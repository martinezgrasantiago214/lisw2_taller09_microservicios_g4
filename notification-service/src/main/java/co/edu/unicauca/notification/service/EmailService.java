package co.edu.unicauca.notification.service;

import co.edu.unicauca.notification.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Simulación del envío de correo. El retardo configurable hace evidente que
 * el envío ocurre fuera del hilo de la petición HTTP que originó el evento.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final long simulatedDelayMs;

    public EmailService(@Value("${notification.simulated-delay-ms:1500}") long simulatedDelayMs) {
        this.simulatedDelayMs = simulatedDelayMs;
    }

    public void sendEmail(Notification notification) {
        try {
            Thread.sleep(simulatedDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.info("[NotificationService] ✉ Correo simulado -> destinatario={} | asunto='{}' | {}",
                notification.getRecipientId() == null ? "administradores" : "user:" + notification.getRecipientId(),
                notification.getSubject(), notification.getMessage());
    }
}
