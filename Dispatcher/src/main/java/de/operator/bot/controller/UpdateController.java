package de.operator.bot.controller;

import de.operator.bot.service.ProducerService;
import de.operator.bot.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.FileInputStream;

@Log4j
@Component
@RequiredArgsConstructor
public class UpdateController {
    private final TelegramBot telegramBot;
    private final ProducerService producerService;
    private final MessageUtils messageUtils;


    public void processUpdate(Update update) {
        if (update.hasMessage() || update.hasCallbackQuery()) {
            producerService.sendToNodeUserUpdate(update);
        } else {
            setUnsupportedMessageType(update);
        }
    }

    private void setUnsupportedMessageType(Update update) {
        var sendMessage = messageUtils.generateSendMessageFromUpdate(update, "This type of message is unsupported");
        telegramBot.sendAnswerSendMessage(sendMessage);
    }

}
