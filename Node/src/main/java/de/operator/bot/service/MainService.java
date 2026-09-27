package de.operator.bot.service;

import de.operator.bot.TaskProcessingErrorEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void handleUpdate(Update update);

    void handleTaskErrorEvent(TaskProcessingErrorEvent taskProcessingErrorEvent);

}
