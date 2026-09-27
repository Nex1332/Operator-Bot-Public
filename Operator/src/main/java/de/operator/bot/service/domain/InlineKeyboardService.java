package de.operator.bot.service.domain;


import de.operator.bot.entity.Task;
import de.operator.bot.entity.TaskInfo;
import de.operator.bot.service.application.enums.ServiceButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public interface InlineKeyboardService {

    InlineKeyboardMarkup createMainMenu();

    InlineKeyboardMarkup createTasksMenu(List<Task> tasks, ServiceButton serviceButton);

    InlineKeyboardMarkup createTaskMenu(Integer taskId, ServiceButton serviceButton);

    InlineKeyboardMarkup createInfoMenu(Integer taskId, boolean isAvailable);

    InlineKeyboardMarkup createInfosMenu(List<TaskInfo> taskInfos, boolean isAvailable);
}
