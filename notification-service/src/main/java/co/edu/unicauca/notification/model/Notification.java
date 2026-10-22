package co.edu.unicauca.notification.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Usuario destinatario (null = administradores del banco). */
    private Long recipientId;

    private Long questionId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String subject;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime sentAt;

    protected Notification() {
    }

    public Notification(Long recipientId, Long questionId, NotificationType type, String subject, String message) {
        this.recipientId = recipientId;
        this.questionId = questionId;
        this.type = type;
        this.subject = subject;
        this.message = message;
        this.status = NotificationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
    }

    public Long getId() { return id; }
    public Long getRecipientId() { return recipientId; }
    public Long getQuestionId() { return questionId; }
    public NotificationType getType() { return type; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public NotificationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSentAt() { return sentAt; }
}
