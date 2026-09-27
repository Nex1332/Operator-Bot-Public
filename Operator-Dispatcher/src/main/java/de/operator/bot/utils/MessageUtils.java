package de.operator.bot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
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

    public SendMessage generateSendMessageFromSendMessage(SendMessage sendMessage, String text) {
        String chatId = sendMessage.getChatId();

        return getSendMessage(sendMessage, text, chatId);
    }

    public SendMessage generateSendMessageFromEditMessageText(EditMessageText editMessageText, String text) {
        String chatId = editMessageText.getChatId();

        return getSendMessage(new SendMessage(), text, chatId);
    }

    public SendMessage generateSendMessageFromForwardMessage(ForwardMessage forwardMessage, String text) {
        String chatId = forwardMessage.getChatId();

        return getSendMessage(new SendMessage(), text, chatId);
    }

    private static SendMessage getSendMessage(SendMessage sendMessage, String text, String chatId) {
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return sendMessage;
    }

    public SendMessage generateSendMessage(Long chatId, String text) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        return sendMessage;
    }
}
