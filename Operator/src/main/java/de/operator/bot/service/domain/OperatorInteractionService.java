package de.operator.bot.service.domain;

import de.operator.bot.entity.Operator;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface OperatorInteractionService {

    SendMessage handleRegistrationCommand(Update update, Operator operator);

    SendMessage processRegistrationCode(Update update, Operator operator);

    SendMessage handleMainMenuCommand(Update update, Operator operator);

    EditMessageText processAvailableTasksButton(Update update);

    EditMessageText processMyTasksButton(Update update);

    EditMessageText processMyCompletedTaskButton(Update update, Integer taskId);

    EditMessageText processMyCompletedTasksButton(Update update, Operator operator);

    EditMessageText processAvailableTaskButton(Update update, Integer taskId);

    EditMessageText processMyTaskButton(Update update, Integer taskId);

    EditMessageText processMainMenuButton(Update update);
}
