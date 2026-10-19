package cl.duoc.andesstay.notify.producer;

import cl.duoc.andesstay.notify.config.RabbitTopology;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);
    private final RabbitTemplate rabbitTemplate;

    public NotificationProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(String routingKey, Map<String, Object> payload) {
        log.info("Publicando mensaje en Exchange [{}] con RoutingKey [{}]: {}",
                RabbitTopology.EXCHANGE_CMD_DIRECT, routingKey, payload);

        rabbitTemplate.convertAndSend(RabbitTopology.EXCHANGE_CMD_DIRECT, routingKey, payload);
    }
}