package de.operator.bot.service;

import de.operator.bot.BotResponse;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface ConsumerService {
    void consumeFromOperatorResponseSendMessage(SendMessage sendMessage);

    void consumeFromOperatorResponseEditMessageText(EditMessageText editMessageText);

    void consumeFromOperatorBotResponse(BotResponse sendPhoto);

    void consumeFromOperatorDeleteMessage(DeleteMessage deleteMessage);
}
