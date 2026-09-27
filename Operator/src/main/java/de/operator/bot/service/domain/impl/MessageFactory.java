package de.operator.bot.service.domain.impl;

import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public class MessageFactory {

    public static EditMessageText createEditMessageText(String text, Long chatId, Integer messageId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return EditMessageText.builder()
                .chatId(chatId)
                .text(text)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    public static EditMessageText createEditMessageText(String text, Long chatId, Integer messageId) {
        return EditMessageText.builder()
                .chatId(chatId)
                .text(text)
                .messageId(messageId)
                .build();
    }

    public static SendMessage createSendMessage(String text, Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }

    public static SendMessage createSendMessage(String text, Long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    public static SendMessage createSendMessage(String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendMessage.builder()
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .chatId("")
                .build();
    }

    public static ForwardMessage createForwardMessage(Long fromChatId, Long chatId, Integer messageId) {
        return ForwardMessage.builder()
                .fromChatId(fromChatId)
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }
}
