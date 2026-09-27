package de.operator.bot.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Configuration
public class RabbitConfiguration {
    @Value("${spring.rabbitmq.queues.dispatcher_node_user-update-queue}")
    private String dispatcherNodeUserUpdateQueue;
    @Value("${spring.rabbitmq.queues.payment_node_payment-feedback-queue}")
    private String paymentNodePaymentFeedbackQueue;
    @Value("${spring.rabbitmq.queues.task_node_response-send-message-queue}")
    private String taskNodeResponseSendMessageQueue;
    @Value("${spring.rabbitmq.queues.task_node_task-error-event-queue}")
    private String taskNodeTaskErrorEventQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue dispatcherNodeUserUpdateQueue() {
        return new Queue(dispatcherNodeUserUpdateQueue);
    }

    @Bean
    public Queue paymentNodePaymentFeedbackQueue() {
        return new Queue(paymentNodePaymentFeedbackQueue);
    }

    @Bean
    public Queue taskNodeResponseSendMessageQueue() {
        return new Queue(taskNodeResponseSendMessageQueue);
    }

    @Bean
    public Queue taskNodeCharErrorMessageQueue() {
        return new Queue(taskNodeTaskErrorEventQueue);
    }
}