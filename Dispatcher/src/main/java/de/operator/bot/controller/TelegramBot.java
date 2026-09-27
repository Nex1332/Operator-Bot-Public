package de.operator.bot.controller;

import de.operator.bot.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.List;

@Log4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramWebhookBot {
    @Value("${bot.name}")
    private String botName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.uri}")
    private String botUri;
    private final MessageUtils messageUtils;

    @PostConstruct
    public void init() {
        List<BotCommand> commands = List.of(
                new BotCommand("/start", "Запуск бота")
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

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }
}