package de.operator.bot.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Value("${spring.rabbitmq.queues.payment_task_payment-status-queue}")
    private String paymentTaskPaymentStatusQueue;
    @Value("${spring.rabbitmq.queues.node_task_request-process-task-queue}")
    private String nodeTaskRequestProcessTaskQueue;
    @Value("${spring.rabbitmq.queues.node_task_request-cancel-task-queue}")
    private String nodeTaskRequestCancelTaskQueue;
    @Value("${spring.rabbitmq.queues.node_task_payment-status-queue}")
    private String nodeTaskPaymentStatusQueue;
    @Value("${spring.rabbitmq.queues.operator_task_response-answer-task-answer-message-queue}")
    private String operatorTaskResponseAnswerTaskAnswerMessageQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue paymentTaskPaymentStatusQueue() {
        return new Queue(paymentTaskPaymentStatusQueue);
    }

    @Bean
    public Queue nodeTaskRequestProcessTaskQueue() {
        return new Queue(nodeTaskRequestProcessTaskQueue);
    }

    @Bean
    public Queue nodeTaskRequestCancelTaskQueue() {
        return new Queue(nodeTaskRequestCancelTaskQueue);
    }

    @Bean
    public Queue nodeTaskPaymentStatusQueue() {
        return new Queue(nodeTaskPaymentStatusQueue);
    }

    @Bean
    public Queue operatorTaskResponseAnswerTaskAnswerMessageQueue() {
        return new Queue(operatorTaskResponseAnswerTaskAnswerMessageQueue);
    }
}
