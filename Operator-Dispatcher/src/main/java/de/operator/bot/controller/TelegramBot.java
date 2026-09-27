package de.operator.bot.controller;

import de.operator.bot.BotResponse;
import de.operator.bot.entity.enums.FileType;
import de.operator.bot.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

@Log4j
@Component
public class TelegramBot extends TelegramWebhookBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.uri}")
    private String botUri;
    private final MessageUtils messageUtils;

    public TelegramBot(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    @PostConstruct
    public void init() {
        List<BotCommand> commands = List.of(
                new BotCommand("/registration", "Регистрация"),
                new BotCommand("/main_menu", "Главное Меню")
        );

        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(commands);
        setMyCommands.setScope(new BotCommandScopeDefault());

        try {
            var setWebhook = SetWebhook.builder()
                    .url(botUri)
                    .build();
            this.setWebhook(setWebhook);
            this.execute(setMyCommands);
        } catch (TelegramApiException e) {
            log.error("Something went wrong during initialization! Error " + e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }

    public void sendAnswerSendMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Failed to send message, something went wrong! Error: " + e);

                var errorMessage = messageUtils.generateSendMessageFromSendMessage(message, "Что-то Пошло не так. Свяжитесь с поддержкой /support");
                sendAnswerSendMessage(errorMessage);
            }
        }
    }

    public void sendAnswerEditMessageText(EditMessageText editMessageText) {
        if (editMessageText != null) {
            try {
                execute(editMessageText);
            } catch (TelegramApiException e) {
                log.error("Failed to send message, something went wrong! Error: " + e);

                var errorMessage = messageUtils.generateSendMessageFromEditMessageText(editMessageText, "Что-то Пошло не так. Свяжитесь с поддержкой /support");
                sendAnswerSendMessage(errorMessage);
            }
        }
    }

    @Async
    public void processBotResponse(BotResponse botResponse) {
        FileType fileType = botResponse.type();
        Long chatId = botResponse.chatId();
        InlineKeyboardMarkup inlineKeyboardMarkup = botResponse.inlineKeyboardMarkup();

        File file = new File(botResponse.info());
        InputFile inputFile = new InputFile(file);

        try {
            switch (fileType) {
                case VOICE -> createAndSendSendVoice(inputFile, chatId, inlineKeyboardMarkup);
                case DOCUMENT -> createAndSendSendDocument(inputFile, chatId, inlineKeyboardMarkup);
                case PHOTO -> createAndSendSendPhoto(inputFile, chatId, inlineKeyboardMarkup);
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send file message, something went wrong! Error: " + e);

            var errorMessage = messageUtils.generateSendMessage(chatId, "Что-то Пошло не так. Свяжитесь с поддержкой /support");
            sendAnswerSendMessage(errorMessage);
        }
    }

    public void createAndSendSendVoice(InputFile inputFile, Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) throws TelegramApiException {
        execute(SendVoice.builder()
                .voice(inputFile)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }

    public void createAndSendSendDocument(InputFile inputFile, Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) throws TelegramApiException {
        execute(SendDocument.builder()
                .document(inputFile)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }

    public void createAndSendSendPhoto(InputFile inputFile, Long chatId, InlineKeyboardMarkup inlineKeyboardMarkup) throws TelegramApiException {
        execute(SendPhoto.builder()
                .photo(inputFile)
                .chatId(chatId)
                .replyMarkup(inlineKeyboardMarkup)
                .build());
    }

    public void sendDeleteMessage(DeleteMessage deleteMessage) {
        if (deleteMessage != null) {
            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                log.error("Failed to send message, something went wrong! Error: " + e);
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }
}