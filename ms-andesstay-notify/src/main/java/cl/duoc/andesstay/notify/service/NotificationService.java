package cl.duoc.andesstay.notify.service;

import cl.duoc.andesstay.notify.config.RabbitTopology;
import cl.duoc.andesstay.notify.dto.NotificationRequest;
import cl.duoc.andesstay.notify.producer.NotificationProducer;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationProducer notificationProducer;
    private final AtomicLong sequence = new AtomicLong();

    public NotificationService(NotificationProducer notificationProducer) {
        this.notificationProducer = notificationProducer;
    }

    public String enqueue(NotificationRequest request) {
        String ticket = "NTF-" + sequence.incrementAndGet();

        String routingKey = switch (request.getType().toLowerCase()) {
            case "housekeeping" -> RabbitTopology.Q_HOUSEKEEPING;
            case "voucher" -> RabbitTopology.Q_VOUCHER;
            default -> RabbitTopology.Q_EMAIL;
        };

        Map<String, Object> payload = Map.of(
                "ticket", ticket,
                "type", request.getType(),
                "message", request.getMessage(),
                "recipient", request.getRecipient() != null ? request.getRecipient() : "N/A"
        );

        // Llama al productor dedicado
        notificationProducer.sendNotification(routingKey, payload);

        return ticket;
    }
}