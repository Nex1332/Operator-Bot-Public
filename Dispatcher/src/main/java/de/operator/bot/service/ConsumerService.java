package de.operator.bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface ConsumerService {
    void consumeFromNodeResponseSendMessage(SendMessage sendMessage);
    void consumeFromNodeResponseEditMessageText(EditMessageText editMessageText);
}
