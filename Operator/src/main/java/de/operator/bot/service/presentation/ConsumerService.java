package de.operator.bot.service.presentation;

import de.operator.bot.entity.Task;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeFromOperatorDispatcherOperatorUpdate(Update update);
    void consumeFromPaymentHotException(String string);
    void consumeFromTaskNewTaskEvent(Task task);
}
