package co.edu.unicauca.notification.controller;

import co.edu.unicauca.notification.dto.NotificationDTO;
import co.edu.unicauca.notification.model.NotificationType;
import co.edu.unicauca.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> find(@RequestParam(required = false) Long recipientId,
                                                      @RequestParam(required = false) Long questionId,
                                                      @RequestParam(required = false) NotificationType type) {
        return ResponseEntity.ok(notificationService.find(recipientId, questionId, type));
    }
}
