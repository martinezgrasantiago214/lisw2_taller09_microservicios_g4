package co.edu.unicauca.notification.repository;

import co.edu.unicauca.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
