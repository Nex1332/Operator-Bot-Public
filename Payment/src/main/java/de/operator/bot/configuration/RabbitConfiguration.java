package de.operator.bot.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Value("${spring.rabbitmq.queues.node_payment_request-create-payment-link-queue}")
    private String nodePaymentRequestCreatePaymentLinkQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue nodePaymentRequestCreatePaymentLinkQueue() {
        return new Queue(nodePaymentRequestCreatePaymentLinkQueue);
    }
}