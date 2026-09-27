package de.operator.bot.service.presentation;

import de.operator.bot.BotResponse;
import de.operator.bot.TaskAnswerMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface ProducerService {

    void sendToOperatorDispatcherResponseSendMessage(SendMessage sendMessage);

    void sendToOperatorDispatcherResponseEditMessageText(EditMessageText editMessageText);

    void sendToTaskResponseAnswerTaskAnswerMessage(TaskAnswerMessage taskAnswerMessage);

    void sendToOperatorDispatcherDeleteMessage(DeleteMessage deleteMessage);

    void sendToOperatorDispatcherBotResponse(BotResponse botResponse);
}
