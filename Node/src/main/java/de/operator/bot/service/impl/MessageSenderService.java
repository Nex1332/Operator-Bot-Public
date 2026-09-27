package de.operator.bot.service.impl;

import de.operator.bot.entity.AppUser;
import de.operator.bot.service.ProducerService;
import de.operator.bot.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageSenderService {
    private final ProducerService producerService;
    private final TranslationService translationService;

    public void createAndSendEditMessageTextToDispatcher(String key, Locale locale, Long chatId, Integer messageId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        createAndSendEditMessageTextToDispatcher(chatId, messageId, key, locale, inlineKeyboardMarkup, true);
    }

    public void createAndSendEditMessageTextToDispatcher(Long chatId, Integer messageId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        createAndSendEditMessageTextToDispatcher(chatId, messageId, text, null, inlineKeyboardMarkup, false);
    }

    public void createAndSendEditMessageTextToDispatcher(
            Long chatId,
            Integer messageId,
            String keyOrText,
            Locale locale,
            InlineKeyboardMarkup inlineKeyboardMarkup,
            boolean translate

    ) {
        String text = translate ? translationService.translate(keyOrText, locale) : keyOrText;

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .build();

        producerService.sendToDispatcherResponseEditMessageText(editMessageText);
    }

    public void createAndSendSendMessageToDispatcherWithTranslate(
            String key,
            Locale locale,
            Long chatId,
            InlineKeyboardMarkup inlineKeyboardMarkup,
            Object... args
    ) {
        String translatedText = (args != null && args.length > 0)
                ? translationService.translate(key, locale, args)
                : translationService.translate(key, locale);

        SendMessage.SendMessageBuilder builder = SendMessage.builder()
                .chatId(chatId)
                .text(translatedText);

        if (inlineKeyboardMarkup != null) {
            builder.replyMarkup(inlineKeyboardMarkup);
        }

        SendMessage sendMessage = builder.build();
        producerService.sendToDispatcherResponseSendMessage(sendMessage);
    }


    public void createAndSendSendMessageToDispatcher(Long chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage editMessageText = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(inlineKeyboardMarkup)
                .build();

        producerService.sendToDispatcherResponseSendMessage(editMessageText);
    }

    public void unsupportedMessageType(Long chatId, AppUser appUser) {
        producerService.sendToDispatcherResponseSendMessage(SendMessage.builder()
                .chatId(chatId)
                .text(translationService.translate("unsupported.message.type", new Locale(appUser.getLanguageCode())))
                .build());
    }

    public void createAndSendEditMessageTextWithParseModeToDispatcher(
            String key,
            Locale locale,
            Long chatId,
            Integer messageId,
            String parseMode,
            InlineKeyboardMarkup inlineKeyboardMarkup,
            Object... args
    ) {
        String text = (args != null && args.length > 0)
                ? translationService.translate(key, locale, args)
                : translationService.translate(key, locale);

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(parseMode)
                .messageId(messageId)
                .replyMarkup(inlineKeyboardMarkup)
                .build();

        producerService.sendToDispatcherResponseEditMessageText(editMessageText);
    }
}