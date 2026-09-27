package de.operator.bot.service.presentation.impl;

import de.operator.bot.BotResponse;
import de.operator.bot.TaskAnswerMessage;
import de.operator.bot.service.presentation.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    @Value("${spring.rabbitmq.queues.operator_operatordispatcher_response-send-message-queue}")
    private String operatorOperatorDispatcherResponseSendMessageQueue;
    @Value("${spring.rabbitmq.queues.operator_operatordispatcher_response-edit-message-text-queue}")
    private String operatorOperatorDispatcherResponseEditMessageTextQueue;
    @Value("${spring.rabbitmq.queues.operator_task_response-answer-task-answer-message-queue}")
    private String operatorTaskResponseAnswerTaskAnswerMessageQueue;
    @Value("${spring.rabbitmq.queues.operator_operatordispatcher_delete-message-queue}")
    private String operatorOperatorDispatcherDeleteMessageQueue;
    @Value("${spring.rabbitmq.queues.operator_operatordispatcher_bot-response-queue}")
    private String operatorOperatorDispatcherBotResponseQueue;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendToOperatorDispatcherResponseSendMessage(SendMessage sendMessage) {
        log.debug("Sending to operator-dispatcher Response SendMessage");
        rabbitTemplate.convertAndSend(operatorOperatorDispatcherResponseSendMessageQueue, sendMessage);
    }

    @Override
    public void sendToOperatorDispatcherResponseEditMessageText(EditMessageText editMessageText) {
        log.debug("Sending to operator-dispatcher Response EditMessageText");
        rabbitTemplate.convertAndSend(operatorOperatorDispatcherResponseEditMessageTextQueue, editMessageText);
    }

    @Override
    public void sendToTaskResponseAnswerTaskAnswerMessage(TaskAnswerMessage taskAnswerMessage) {
        log.debug("Sending to Task Response Answer TaskAnswerMessage");
        rabbitTemplate.convertAndSend(operatorTaskResponseAnswerTaskAnswerMessageQueue, taskAnswerMessage);
    }

    @Override
    public void sendToOperatorDispatcherBotResponse(BotResponse botResponse) {
        log.debug("Sending to OperatorDispatcher BotResponse");
        rabbitTemplate.convertAndSend(operatorOperatorDispatcherBotResponseQueue, botResponse);
    }

    @Override
    public void sendToOperatorDispatcherDeleteMessage(DeleteMessage deleteMessage) {
        log.debug("Sending to OperatorDispatcher DeleteMessage");
        rabbitTemplate.convertAndSend(operatorOperatorDispatcherDeleteMessageQueue, deleteMessage);
    }
}