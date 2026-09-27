package de.operator.bot.service.application;

import de.operator.bot.entity.Task;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
    void handleUpdate(Update update);

    void handleNewTaskEvent(Task task);

//    TODO Добавить обработку новой дополнительной ифнормации
}
