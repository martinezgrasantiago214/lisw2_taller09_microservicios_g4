package co.edu.unicauca.notification.service;

import co.edu.unicauca.notification.dto.NotificationDTO;
import co.edu.unicauca.notification.model.Notification;
import co.edu.unicauca.notification.model.NotificationType;
import co.edu.unicauca.notification.repository.NotificationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    /** Registra la notificación, simula el envío y la marca como SENT (o FAILED). */
    public Notification notify(Long recipientId, Long questionId, NotificationType type, String subject, String message) {
        Notification notification = repository.save(new Notification(recipientId, questionId, type, subject, message));
        try {
            emailService.sendEmail(notification);
            notification.markAsSent();
        } catch (RuntimeException ex) {
            notification.markAsFailed();
            repository.save(notification);
            throw ex; // el listener reintenta y, si persiste, el mensaje va a la DLQ
        }
        return repository.save(notification);
    }

    public List<NotificationDTO> find(Long recipientId, Long questionId, NotificationType type) {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .filter(n -> recipientId == null || Objects.equals(recipientId, n.getRecipientId()))
                .filter(n -> questionId == null || Objects.equals(questionId, n.getQuestionId()))
                .filter(n -> type == null || type == n.getType())
                .map(NotificationDTO::from)
                .toList();
    }
}
