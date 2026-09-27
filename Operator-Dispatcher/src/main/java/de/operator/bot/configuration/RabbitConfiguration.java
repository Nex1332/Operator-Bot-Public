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
    @Value("${spring.rabbitmq.queues.operator_operatordispatcher_response-send-message-queue}")
    private String operatorOperatorDispatcherResponseSendMessageQueue;
    @Value("${spring.rabbitmq.queues.operator_operatordispatcher_response-edit-message-text-queue}")
    private String operatorOperatorDispatcherResponseEditMessageTextQueue;
    @Value("${spring.rabbitmq.queues.operator_operatordispatcher_delete-message-queue}")
    private String operatorOperatorDispatcherDeleteMessageQueue;
    @Value("${spring.rabbitmq.queues.operator_operatordispatcher_bot-response-queue}")
    private String operatorOperatorDispatcherBotResponseQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue operatorOperatorDispatcherResponseSendMessageQueue() {
        return new Queue(operatorOperatorDispatcherResponseSendMessageQueue);
    }

    @Bean
    public Queue operatorOperatorDispatcherResponseEditMessageTextQueue() {
        return new Queue(operatorOperatorDispatcherResponseEditMessageTextQueue);
    }

    @Bean
    public Queue operatorOperatorDispatcherDeleteMessageQueue() {
        return new Queue(operatorOperatorDispatcherDeleteMessageQueue);
    }

    @Bean
    public Queue operatorOperatorDispatcherBotResponseQueue() {
        return new Queue(operatorOperatorDispatcherBotResponseQueue);
    }
}
