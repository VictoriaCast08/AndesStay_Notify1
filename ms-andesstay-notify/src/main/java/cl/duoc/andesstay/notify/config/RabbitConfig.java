package cl.duoc.andesstay.notify.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // --- CONSTANTES DE TOPOLOGÍA ---
    public static final String EXCHANGE_CMD_DIRECT = "ex.andesstay.cmd";
    public static final String EXCHANGE_DEAD = "ex.andesstay.dlx";

    public static final String Q_EMAIL = "q.cmd.email";
    public static final String Q_EMAIL_DLQ = "q.cmd.email.dlq";
    public static final String RK_EMAIL = "rk.cmd.email";

    // --- CONVERTER JSON ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // --- EXCHANGES ---
    @Bean
    public DirectExchange cmdDirectExchange() {
        return new DirectExchange(EXCHANGE_CMD_DIRECT);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DEAD);
    }

    // --- DEAD LETTER QUEUE (DLQ) ---
    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(Q_EMAIL_DLQ)
                .quorum() // Replicación en el clúster de 2 nodos
                .build();
    }

    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq())
                .to(deadLetterExchange())
                .with(Q_EMAIL_DLQ);
    }

    // --- COLA PRINCIPAL (Conectada a DLQ) ---
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(Q_EMAIL)
                .quorum() // Replicación en el clúster de 2 nodos
                .withArgument("x-dead-letter-exchange", EXCHANGE_DEAD)
                .withArgument("x-dead-letter-routing-key", Q_EMAIL_DLQ)
                .build();
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder.bind(emailQueue())
                .to(cmdDirectExchange())
                .with(RK_EMAIL);
    }
}