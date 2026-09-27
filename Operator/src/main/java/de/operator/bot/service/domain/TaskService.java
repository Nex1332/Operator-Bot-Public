package de.operator.bot.service.domain;


import de.operator.bot.BotResponse;
import de.operator.bot.TaskAnswerMessage;
import de.operator.bot.entity.Operator;
import de.operator.bot.entity.Task;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface TaskService {

    EditMessageText processInfosButton(Update update, Integer taskId);

    BotResponse processInfoButton(Update update, Long taskInfoId);

    EditMessageText processAcceptTaskButton(Update update, Integer taskId);

    EditMessageText processGiveAnswerButton(Update update, Integer taskId, Operator operator);

    TaskAnswerMessage processAnswer(Update update, Operator operator);

    SendMessage processNewTask(Task task, String text);
}
