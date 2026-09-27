package de.operator.bot.service.domain.impl;

import de.operator.bot.entity.Task;
import de.operator.bot.entity.TaskInfo;
import de.operator.bot.service.application.enums.ServiceButton;
import de.operator.bot.service.domain.InlineKeyboardService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static de.operator.bot.service.application.enums.ServiceButton.*;

@Service
public class InlineKeyboardServiceImpl implements InlineKeyboardService {

    @Override
    public InlineKeyboardMarkup createMainMenu() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(button("Доступные Задачи", AVAILABLE_TASKS.toString())));
        rows.add(List.of(button("Мои Задачи", MY_TASKS.toString())));
        rows.add(List.of(button("Мои Выполненые Задачи", MY_COMPLETED_TASKS.toString())));

        return setRows(rows);
    }

    @Override
    public InlineKeyboardMarkup createTasksMenu(List<Task> tasks, ServiceButton buttonType) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Task task : tasks) {
            String emoji = "📌 ";
            String taskTitleWithEmoji = emoji + task.getTaskTitle();
            rows.add(List.of(button(taskTitleWithEmoji, buttonType + "|" + task.getTaskId())));
        }

        rows.add(List.of(button("Главное Меню", MAIN_MENU.toString())));

        return setRows(rows);
    }

    @Override
    public InlineKeyboardMarkup createTaskMenu(Integer taskId, ServiceButton buttonType) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(button("Список Информации", INFOS + "|" + taskId)));

        switch (buttonType) {
            case AVAILABLE_TASK -> rows.add(List.of(button("Принять Задачу", ACCEPT_TASK + "|" + taskId)));
            case MY_TASK -> rows.add(List.of(button("Дать Ответ", GIVE_ANSWER + "|" + taskId)));
        }

        rows.add(List.of(button("Главное меню", MAIN_MENU.toString())));

        return setRows(rows);
    }

    @Override
    public InlineKeyboardMarkup createInfosMenu(List<TaskInfo> taskInfos, boolean isAvailable) {
        Integer taskId = taskInfos.get(0).getTask().getTaskId();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        int infoNumber = 1;
        for (TaskInfo taskInfo : taskInfos) {
            rows.add(List.of(button("Информация № " + infoNumber, INFO + "|" + taskInfo.getId())));
            infoNumber++;
        }

        if (isAvailable) {
            addAvailableTaskAndAvailableTasksButtons(rows, taskId);
        } else {
            addMyTaskAndMyTasksButtons(rows, taskId);
        }

        return setRows(rows);
    }

    @Override
    public InlineKeyboardMarkup createInfoMenu(Integer taskId, boolean isAvailable) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        if (isAvailable) {
            addAvailableTaskAndAvailableTasksButtons(rows, taskId);
            rows.add(List.of(button("Принять Задачу", ACCEPT_TASK + "|" + taskId)));
        } else {
            addMyTaskAndMyTasksButtons(rows, taskId);
        }
        rows.add(List.of(button("Главное меню", MAIN_MENU.toString())));

        return setRows(rows);
    }

    private static InlineKeyboardMarkup setRows(List<List<InlineKeyboardButton>> rows) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }

    private void addMyTaskAndMyTasksButtons(List<List<InlineKeyboardButton>> rows, Integer taskId) {
        rows.add(List.of(button("Моя Задача", MY_TASK + "|" + taskId)));
        rows.add(List.of(button("Мои Задачи", MY_TASKS.toString())));
    }

    private void addAvailableTaskAndAvailableTasksButtons(List<List<InlineKeyboardButton>> rows, Integer taskId) {
        rows.add(List.of(button("Доступная Задача", AVAILABLE_TASK + "|" + taskId)));
        rows.add(List.of(button("Доступные Задачи", AVAILABLE_TASKS.toString())));
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }
}
