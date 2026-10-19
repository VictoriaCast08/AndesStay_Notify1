package cl.duoc.andesstay.notify.web;

import cl.duoc.andesstay.notify.dto.NotificationRequest;
import cl.duoc.andesstay.notify.service.NotificationService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> sendNotification(@Valid @RequestBody NotificationRequest request) {
        String ticket = notificationService.enqueue(request);
        return ResponseEntity.ok(Map.of(
                "status", "ENQUEUED",
                "ticket", ticket
        ));
    }
}