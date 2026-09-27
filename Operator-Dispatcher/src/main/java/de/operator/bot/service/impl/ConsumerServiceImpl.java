package de.operator.bot.service.impl;

import de.operator.bot.BotResponse;
import de.operator.bot.controller.TelegramBot;
import de.operator.bot.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final TelegramBot telegramBot;

    @RabbitListener(queues = "${spring.rabbitmq.queues.operator_operatordispatcher_response-send-message-queue}")
    @Override
    public void consumeFromOperatorResponseSendMessage(SendMessage sendMessage) {
        log.debug("Consuming from Node Response SendMessage");
        telegramBot.sendAnswerSendMessage(sendMessage);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.operator_operatordispatcher_response-edit-message-text-queue}")
    @Override
    public void consumeFromOperatorResponseEditMessageText(EditMessageText editMessageText) {
        log.debug("Consuming from Node Response EditMessageText");
        telegramBot.sendAnswerEditMessageText(editMessageText);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.operator_operatordispatcher_bot-response-queue}")
    @Override
    public void consumeFromOperatorBotResponse(BotResponse botResponse) {
        log.debug("Consuming from Operator");
        telegramBot.processBotResponse(botResponse);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.operator_operatordispatcher_delete-message-queue}")
    @Override
    public void consumeFromOperatorDeleteMessage(DeleteMessage deleteMessage) {
        log.debug("Consuming from Operator DeleteMessage");
        telegramBot.sendDeleteMessage(deleteMessage);
    }
}
