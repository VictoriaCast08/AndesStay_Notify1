package cl.duoc.andesstay.notify.service;

import cl.duoc.andesstay.notify.config.RabbitConfig;
import cl.duoc.andesstay.notify.dto.NotificationRequest;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

// Servicio para el procesamiento y simulación de envío de notificaciones
@Service
public class NotificationService {

    private final RabbitTemplate rabbitTemplate;
    private final AtomicLong sequence = new AtomicLong();

    public NotificationService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public String enqueue(NotificationRequest request) {
        String ticket = "NTF-" + sequence.incrementAndGet();

        // Determina la Routing Key según el tipo de notificación
        String routingKey = switch (request.getType().toLowerCase()) {
            case "housekeeping" -> "rk.cmd.housekeeping";
            case "voucher" -> "rk.cmd.voucher";
            default -> RabbitConfig.RK_EMAIL;
        };

        Map<String, Object> payload = Map.of(
                "ticket", ticket,
                "type", request.getType(),
                "message", request.getMessage(),
                "recipient", request.getRecipient() != null ? request.getRecipient() : "N/A"
        );

        // Envía directamente al exchange genérico de comandos
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_CMD_DIRECT, routingKey, payload);

        return ticket;
    }
}