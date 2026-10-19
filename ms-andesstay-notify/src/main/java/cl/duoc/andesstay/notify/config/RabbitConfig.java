package cl.duoc.andesstay.notify.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // --- Converter para enviar objetos JSON en las colas ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // --- EXCHANGES ---
    @Bean
    public DirectExchange cmdDirectExchange() {
        return new DirectExchange(RabbitTopology.EXCHANGE_CMD_DIRECT);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitTopology.EXCHANGE_DEAD);
    }

    // --- DLQs (Dead Letter Queues) ---
    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(RabbitTopology.Q_EMAIL_DLQ).build();
    }

    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq())
                .to(deadLetterExchange())
                .with(RabbitTopology.Q_EMAIL_DLQ);
    }

    // --- COLA PRINCIPAL (Conectada a DLQ) ---
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(RabbitTopology.Q_EMAIL)
                .withArgument("x-dead-letter-exchange", RabbitTopology.EXCHANGE_DEAD)
                .withArgument("x-dead-letter-routing-key", RabbitTopology.Q_EMAIL_DLQ)
                .build();
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(cmdDirectExchange())
                .with(RabbitTopology.Q_EMAIL);
    }
}