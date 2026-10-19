package cl.duoc.andesstay.notify.service;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminService(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    public void createQueue(String name, boolean durable) {
        Queue queue = new Queue(name, durable);
        amqpAdmin.declareQueue(queue);
    }

    public boolean deleteQueue(String name) {
        return amqpAdmin.deleteQueue(name);
    }

    public void createExchange(String name) {
        amqpAdmin.declareExchange(new DirectExchange(name));
    }

    public boolean deleteExchange(String name) {
        return amqpAdmin.deleteExchange(name);
    }
}