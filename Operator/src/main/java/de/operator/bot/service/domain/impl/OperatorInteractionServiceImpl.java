package de.operator.bot.service.domain.impl;

import de.operator.bot.cache.OperatorCacheService;
import de.operator.bot.entity.Operator;
import de.operator.bot.entity.Task;
import de.operator.bot.repository.TaskRepository;
import de.operator.bot.service.domain.InlineKeyboardService;
import de.operator.bot.service.domain.OperatorInteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static de.operator.bot.entity.enums.OperatorsBotState.REGISTERED;
import static de.operator.bot.entity.enums.OperatorsBotState.WAIT_FOR_REGISTRATION_CODE_STATE;
import static de.operator.bot.service.application.enums.ServiceButton.*;
import static de.operator.bot.service.domain.impl.MessageFactory.createEditMessageText;
import static de.operator.bot.service.domain.impl.MessageFactory.createSendMessage;

@RequiredArgsConstructor
@Service
public class OperatorInteractionServiceImpl implements OperatorInteractionService {
    private final TaskRepository taskRepository;
    private final InlineKeyboardService inlineKeyboardService;
    private final OperatorCacheService operatorCacheService;
    @Value("${operator.registration.code}")
    private String registrationCode;

    @Override
    public SendMessage handleRegistrationCommand(Update update, Operator operator) {
        operator.setBotState(WAIT_FOR_REGISTRATION_CODE_STATE);

        operatorCacheService.putOperator(operator, operator.getTelegramUserId());

        return createSendMessage("Здраствуйте, пожалуйста введите регистрационный ключ выданный вам!",
                update.getMessage().getChatId());
    }

    @Override
    public SendMessage processRegistrationCode(Update update, Operator operator) {
        String optionalRegistrationCode = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (optionalRegistrationCode.equals(registrationCode)) {
            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createMainMenu();

            operator.setBotState(REGISTERED);

            operatorCacheService.putOperator(operator, operator.getChatId());

            return createSendMessage("Вы были успешно зарегистрированны", chatId, inlineKeyboardMarkup);
        } else return createSendMessage("Простите но данный ключ был не верным, попробуйте снова", chatId);
    }

    @Override
    public SendMessage handleMainMenuCommand(Update update, Operator operator) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createMainMenu();

        if (operator.getBotState().equals(REGISTERED)) {
            return createSendMessage("Главное меню",
                    update.getMessage().getChatId(), inlineKeyboardMarkup);
        } else {
            return createSendMessage("Иди нахуй умник, зарегистрироваться нужно сначала",
                    update.getMessage().getChatId());
        }
    }

    @Override
    public EditMessageText processAvailableTasksButton(Update update) {
        List<Task> availableTasks = taskRepository.findAllByOperatorId(null);

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createTasksMenu(availableTasks, AVAILABLE_TASK);

        return createEditMessageText("Выберите одну из доступных задач",
                update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
    }

    @Override
    public EditMessageText processMyTasksButton(Update update) {
        List<Task> myTasks = taskRepository.findAllByOperatorId(update.getCallbackQuery().getFrom().getId())
                .stream()
                .filter(task -> !task.isComplete())
                .toList();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createTasksMenu(myTasks, MY_TASK);

        if (!myTasks.isEmpty()) {
            return createEditMessageText("Выберите одну из доступных задач",
                    update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
        } else {
            return createEditMessageText("Простите в данный момент нет задач данного типа",
                    update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
        }
    }

    @Override
    public EditMessageText processMyCompletedTasksButton(Update update, Operator operator) {
        List<Task> myCompletedTasks = taskRepository.findAllByOperatorId(operator.getTelegramUserId())
                .stream()
                .filter(Task::isComplete)
                .toList();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createTasksMenu(myCompletedTasks, MY_COMPLETED_TASK);

        if (!myCompletedTasks.isEmpty()) {
            return createEditMessageText("Выберите одну из ваших выполненых задач",
                    update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
        } else {
            return createEditMessageText("Простите в данный момент нет задач данного типа",
                    update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
        }
    }

    @Override
    public EditMessageText processAvailableTaskButton(Update update, Integer taskId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createTaskMenu(taskId, AVAILABLE_TASK);

        return createEditMessageText("Информация о доступной задаче",
                update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
    }

    @Override
    public EditMessageText processMyTaskButton(Update update, Integer taskId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createTaskMenu(taskId, MY_TASK);

        return createEditMessageText("Информация о вашей задаче",
                update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
    }

    @Override
    public EditMessageText processMyCompletedTaskButton(Update update, Integer taskId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createTaskMenu(taskId, MY_COMPLETED_TASK);

        return createEditMessageText("Информация о вашей законченной задаче",
                update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
    }

    @Override
    public EditMessageText processMainMenuButton(Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createMainMenu();

        return createEditMessageText("Главное меню",
                update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
    }
}
