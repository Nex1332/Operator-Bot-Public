package de.operator.bot.service;

import de.operator.bot.TaskProcessingErrorEvent;
import de.operator.bot.entity.Task;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void sendToOperatorNewTaskEvent(Task task);

    void sendToNodeResponseSendMessage(SendMessage sendMessage);

    void sendToNodeTaskErrorEvent(TaskProcessingErrorEvent taskProcessingErrorEvent);
}
