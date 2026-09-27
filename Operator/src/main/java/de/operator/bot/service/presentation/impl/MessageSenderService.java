package de.operator.bot.service.presentation.impl;

import de.operator.bot.BotResponse;
import de.operator.bot.entity.enums.FileType;
import de.operator.bot.service.presentation.ProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

import static de.operator.bot.service.domain.impl.MessageFactory.createSendMessage;

@RequiredArgsConstructor
@Service
public class MessageSenderService {

    private final ProducerService producerService;

    public void sendBotResponseToOperatorDispatcher(BotResponse botResponse, Update update) {
        sendDeleteMessageToOperatorDispatcher(update);

        if (Objects.requireNonNull(botResponse.type()) == FileType.TEXT) {
            producerService.sendToOperatorDispatcherResponseSendMessage(createSendMessage(botResponse.info(), botResponse.chatId(), botResponse.inlineKeyboardMarkup()));
        } else {
            producerService.sendToOperatorDispatcherBotResponse(botResponse);
        }
    }

    public void unsupportedMessageType(Long chatId) {
        producerService.sendToOperatorDispatcherResponseSendMessage(SendMessage.builder()
                .chatId(chatId)
                .text("""
                        Простите, но мы не поддерживаем сообщения данного типа.
                        Введя комманду /help вы можете посмотреть все возможности нашего бота и как ними пользоваться
                        """)
                .build());
    }

    private void sendDeleteMessageToOperatorDispatcher(Update update) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .messageId(update.getCallbackQuery().getMessage().getMessageId())
                .chatId(update.getCallbackQuery().getMessage().getChatId())
                .build();

        producerService.sendToOperatorDispatcherDeleteMessage(deleteMessage);
    }

    public void sendToOperatorDispatcherResponseEditMessageText(EditMessageText editMessageText, Update update) {
        Message message = update.getCallbackQuery().getMessage();

        if (message.hasVoice() || message.hasPhoto() || message.hasDocument()) {
            sendDeleteMessageToOperatorDispatcher(update);

            SendMessage sendMessage = createSendMessage(editMessageText.getText(), Long.valueOf(editMessageText.getChatId()), editMessageText.getReplyMarkup());
            producerService.sendToOperatorDispatcherResponseSendMessage(sendMessage);
        } else {
            producerService.sendToOperatorDispatcherResponseEditMessageText(editMessageText);
        }
    }
}