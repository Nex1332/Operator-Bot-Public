package de.operator.bot.service.domain.impl;

import de.operator.bot.BotResponse;
import de.operator.bot.TaskAnswerMessage;
import de.operator.bot.entity.*;
import de.operator.bot.repository.AppUserRepository;
import de.operator.bot.repository.OperatorRepository;
import de.operator.bot.repository.TaskInfoRepository;
import de.operator.bot.repository.TaskRepository;
import de.operator.bot.service.domain.InlineKeyboardService;
import de.operator.bot.service.domain.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Optional;

import static de.operator.bot.entity.enums.OperatorsBotState.REGISTERED;
import static de.operator.bot.entity.enums.OperatorsBotState.WAIT_FOR_ANSWER_STATE;
import static de.operator.bot.service.application.enums.ServiceButton.AVAILABLE_TASKS;
import static de.operator.bot.service.domain.impl.MessageFactory.*;

@RequiredArgsConstructor
@Service
public class TaskServiceImpl implements TaskService {
    private final InlineKeyboardService inlineKeyboardService;
    private final TaskRepository taskRepository;
    private final TaskInfoRepository taskInfoRepository;
    private final OperatorRepository operatorRepository;
    private final AppUserRepository appUserRepository;

    @Override
    public EditMessageText processInfosButton(Update update, Integer taskId) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Task task = taskRepository.findByTaskId(taskId);
        List<TaskInfo> taskInfos = taskInfoRepository.findAllByTask_TaskId(taskId);

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createInfosMenu(taskInfos, task.getOperatorId() == null);

        return createEditMessageText("Выберите одну из информаций",
                chatId, update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
    }

    @Override
    public BotResponse processInfoButton(Update update, Long taskInfoId) {
        TaskInfo taskInfo = taskInfoRepository.findTaskInfoById(taskInfoId);
        Integer taskId = taskInfo.getTask().getTaskId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Task task = taskRepository.findByTaskId(taskId);

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createInfoMenu(taskId, task.getOperatorId() == null);

        return new BotResponse(chatId, inlineKeyboardMarkup, taskInfo.getInfo(), taskInfo.getFileType());
    }

    @Override
    public EditMessageText processAcceptTaskButton(Update update, Integer taskId) {
        Task task = taskRepository.findByTaskId(taskId);
        task.setOperatorId(update.getCallbackQuery().getFrom().getId());
        taskRepository.save(task);

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createMainMenu();

        return createEditMessageText("Задача была Добавленна в Ваши задачи",
                update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
    }

    @Override
    public EditMessageText processGiveAnswerButton(Update update, Integer taskId, Operator operator) {
        operator.setCurrentTaskId(taskId);
        operator.setBotState(WAIT_FOR_ANSWER_STATE);
        operatorRepository.save(operator);

        return createEditMessageText("Пожалуйста отправьте ответ",
                update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
    }

    @Override
    public TaskAnswerMessage processAnswer(Update update, Operator operator) {
        Task task = taskRepository.findByTaskId(operator.getCurrentTaskId());
        Optional<AppUser> appUserOptional = appUserRepository.findByTelegramUserId(task.getAppUserId());

        if (appUserOptional.isPresent()) {
            AppUser appUser = appUserOptional.get();

            appUser.setCurrentTaskId(task.getTaskId());
            appUserRepository.save(appUser);

            SendMessage sendMessage = createSendMessage(update.getMessage().getText(), appUser.getChatId());

            TaskAnswerMessage taskAnswerMessage = TaskAnswerMessage.builder()
                    .sendMessage(sendMessage)
                    .taskType(task.getTaskType())
                    .build();

            operator.setBotState(REGISTERED);
            operatorRepository.save(operator);

            return taskAnswerMessage;
        }
        return null;
    }

    @Override
    public SendMessage processNewTask(Task task, String text) {
        Integer taskId = task.getTaskId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createTaskMenu(taskId, AVAILABLE_TASKS);

        return createSendMessage(text, inlineKeyboardMarkup);
    }
}
