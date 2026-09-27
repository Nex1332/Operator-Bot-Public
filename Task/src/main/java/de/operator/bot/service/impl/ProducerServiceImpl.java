package de.operator.bot.service.impl;

import de.operator.bot.TaskProcessingErrorEvent;
import de.operator.bot.entity.Task;
import de.operator.bot.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    @Value("${spring.rabbitmq.queues.task_operator_new-task-event-queue}")
    private String taskOperatorNewTaskQueue;
    @Value("${spring.rabbitmq.queues.task_node_task-error-event-queue}")
    private String taskNodeTaskErrorEventQueue;
    @Value("${spring.rabbitmq.queues.task_node_response-send-message-queue}")
    private String taskNodeResponseSendMessage;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendToOperatorNewTaskEvent(Task task) {
        log.debug("Sending to Operator new Task Event");
        rabbitTemplate.convertAndSend(taskOperatorNewTaskQueue, task);
    }

    @Override
    public void sendToNodeTaskErrorEvent(TaskProcessingErrorEvent taskProcessingErrorEvent){
        log.debug("Sending to Node Error Event");
        rabbitTemplate.convertAndSend(taskNodeTaskErrorEventQueue, taskProcessingErrorEvent);
    }

    @Override
    public void sendToNodeResponseSendMessage(SendMessage sendMessage){
        log.debug("Sending to Node Response SendMessage");
        rabbitTemplate.convertAndSend(taskNodeResponseSendMessage, sendMessage);
    }

}
