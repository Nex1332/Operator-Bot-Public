package de.operator.bot.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Value("${spring.rabbitmq.queues.payment_operator_hot-exception-queue}")
    private String paymentOperatorHotExceptionQueue;
    @Value("${spring.rabbitmq.queues.task_operator_new-task-event-queue}")
    private String taskOperatorNewTaskEventQueue;
    @Value("${spring.rabbitmq.queues.operatordispatcher_operator_operator-update-queue}")
    private String operatorDispatcherOperatorOperatorUpdateQueue;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue paymentOperatorHotExceptionQueue() {
        return new Queue(paymentOperatorHotExceptionQueue);
    }

    @Bean
    public Queue taskOperatorNewTaskEventQueue() {
        return new Queue(taskOperatorNewTaskEventQueue);
    }

    @Bean
    public Queue operatorDispatcherOperatorOperatorUpdateQueue() {
        return new Queue(operatorDispatcherOperatorOperatorUpdateQueue);
    }

}
