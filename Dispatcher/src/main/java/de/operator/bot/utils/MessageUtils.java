package de.operator.bot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {

    public SendMessage generateSendMessageFromUpdate(Update update, String text) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            var sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText(text);
            return sendMessage;
        } else if (update.hasCallbackQuery()) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
            sendMessage.setText(text);
            return sendMessage;
        } else if (update.hasEditedMessage()) {
            var sendMessage = new SendMessage();
            sendMessage.setChatId(update.getEditedMessage().getChatId());
            sendMessage.setText(text);
            return sendMessage;
        }
        System.out.println(update);
        return null;
    }

    public SendMessage generateSendMessageFromSendMessage(SendMessage message, String text) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);
        return sendMessage;
    }

    public SendMessage generateSendMessageFromEditMessageText(EditMessageText editMessageText, String text) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(editMessageText.getChatId());
        sendMessage.setText(text);
        return sendMessage;
    }
}
