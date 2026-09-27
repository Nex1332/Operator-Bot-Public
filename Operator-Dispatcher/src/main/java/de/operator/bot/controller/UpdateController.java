package de.operator.bot.controller;

import de.operator.bot.service.ProducerService;
import de.operator.bot.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Component
public class UpdateController {
    private final TelegramBot telegramBot;
    private final ProducerService producerService;
    private final MessageUtils messageUtils;

    public UpdateController(TelegramBot telegramBot, ProducerService producerService, MessageUtils messageUtils) {
        this.telegramBot = telegramBot;
        this.producerService = producerService;
        this.messageUtils = messageUtils;
    }

    public void processUpdate(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {
            producerService.sendToOperatorOperatorUpdate(update);
        } else {
            setUnsupportedMessageType(update);
        }
    }

    private void setUnsupportedMessageType(Update update) {
        var sendMessage = messageUtils.generateSendMessageFromUpdate(update, "This type of message is unsupported");
        telegramBot.sendAnswerSendMessage(sendMessage);
    }

}
