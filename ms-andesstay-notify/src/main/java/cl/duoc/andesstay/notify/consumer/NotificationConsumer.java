package cl.duoc.andesstay.notify.consumer;

import cl.duoc.andesstay.notify.config.RabbitConfig;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// Consumidor asíncrono para notificaciones
@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    // Escucha la cola principal de emails
    @RabbitListener(queues = RabbitConfig.Q_EMAIL)
    public void processEmailNotification(Map<String, Object> payload, Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("Consumiendo notificacion de EMAIL: {}", payload);

            // Simulación de fallo para pruebas de DLQ
            if (payload.get("message") != null && payload.get("message").toString().contains("ERROR")) {
                throw new IllegalArgumentException("Error simulado para probar envio a DLQ");
            }

            // Confirmación explícita exitosa (ACK)
            channel.basicAck(deliveryTag, false);
            log.info("Notificacion procesada exitosamente con ACK.");

        } catch (Exception e) {
            log.error("Error al procesar mensaje: {}. Redirigiendo a DLQ...", e.getMessage());
            // Rechazo explícito (NACK sin requeue -> envía el mensaje a la DLQ)
            channel.basicNack(deliveryTag, false, false);
        }
    }
}