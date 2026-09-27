package de.operator.bot.service.impl;

import de.operator.bot.controller.TelegramBot;
import de.operator.bot.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final TelegramBot telegramBot;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.node_dispatcher_response-send-message-queue}")
    public void consumeFromNodeResponseSendMessage(SendMessage sendMessage) {
        log.debug("Consuming from Node Response SendMessage");
        telegramBot.sendAnswerSendMessage(sendMessage);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.node_dispatcher_response-edit-message-text-queue}")
    public void consumeFromNodeResponseEditMessageText(EditMessageText editMessageText) {
        log.debug("Consuming from Node Response EditMessageText");
        telegramBot.sendAnswerEditMessageText(editMessageText);
    }
}
