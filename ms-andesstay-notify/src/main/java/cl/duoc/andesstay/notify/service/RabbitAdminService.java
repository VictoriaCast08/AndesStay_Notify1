package cl.duoc.andesstay.notify.service;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.stereotype.Service;

// Servicio de administración dinámica de infraestructura en RabbitMQ
@Service
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    // Crea colas Quorum en el clúster
    public void createQueue(String name, boolean durable) {
        Queue queue = durable
                ? QueueBuilder.durable(name).quorum().build()
                : QueueBuilder.nonDurable(name).build();
        amqpAdmin.declareQueue(queue);
    }

    // Elimina una cola existente
    public boolean deleteQueue(String name) {
        return amqpAdmin.deleteQueue(name);
    }

    // Crea un exchange tipo Direct
    public void createExchange(String name) {
        amqpAdmin.declareExchange(new DirectExchange(name));
    }

    // Elimina un exchange existente
    public boolean deleteExchange(String name) {
        return amqpAdmin.deleteExchange(name);
    }
}