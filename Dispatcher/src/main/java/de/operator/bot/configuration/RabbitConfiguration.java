package de.operator.bot.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class RabbitConfiguration {
    @Value("${spring.rabbitmq.queues.operator_dispatcher_termin-information-queue}")
    private String operatorDispatcherTerminInformationQueue;
    @Value("${spring.rabbitmq.queues.node_dispatcher_response-send-message-queue}")
    private String nodeDispatcherResponseSendMessageQueue;
    @Value("${spring.rabbitmq.queues.node_dispatcher_response-edit-message-text-queue}")
    private String nodeDispatcherResponseEditMessageTextQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue operatorDispatcherTerminInformationQueue() {
        return new Queue(operatorDispatcherTerminInformationQueue);
    }

    @Bean
    public Queue nodeDispatcherResponseSendMessageQueue() {
        return new Queue(nodeDispatcherResponseSendMessageQueue);
    }

    @Bean
    public Queue nodeDispatcherResponseEditMessageTextQueue() {
        return new Queue(nodeDispatcherResponseEditMessageTextQueue);
    }
}
