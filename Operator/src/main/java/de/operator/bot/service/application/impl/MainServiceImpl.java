package de.operator.bot.service.application.impl;

import de.operator.bot.TaskAnswerMessage;
import de.operator.bot.cache.OperatorCacheService;
import de.operator.bot.entity.Operator;
import de.operator.bot.entity.Task;
import de.operator.bot.repository.OperatorRepository;
import de.operator.bot.service.application.MainService;
import de.operator.bot.service.domain.InlineKeyboardService;
import de.operator.bot.service.domain.OperatorInteractionService;
import de.operator.bot.service.domain.TaskService;
import de.operator.bot.service.application.enums.ServiceButton;
import de.operator.bot.service.application.enums.ServiceCommand;
import de.operator.bot.service.presentation.impl.MessageSenderService;
import de.operator.bot.service.presentation.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

import static de.operator.bot.entity.enums.OperatorsBotState.*;
import static de.operator.bot.service.domain.impl.MessageFactory.createSendMessage;

@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final OperatorCacheService operatorCacheService;
    private final TaskService taskService;
    private final MessageSenderService messageSenderService;
    private final OperatorInteractionService operatorInteractionService;
    private final ProducerService producerService;
    private final OperatorRepository operatorRepository;
    private final InlineKeyboardService inlineKeyboardService;
    @Value("${operator.group.chat.id}")
    private String groupChatId;

    @Override
    public void handleNewTaskEvent(Task task) {
        if (task.getOperatorId() == null) {
            String text = """
                    Новая Задача
                    Название Задачи:  """ + task.getTaskTitle();

            producerService.sendToOperatorDispatcherResponseSendMessage(createSendMessage(text, Long.valueOf(groupChatId)));

            SendMessage sendMessage = taskService.processNewTask(task, text);
            List<Operator> operators = operatorRepository.findAll();

            for (Operator operator : operators) {
                sendMessage.setChatId(operator.getChatId());
                producerService.sendToOperatorDispatcherResponseSendMessage(sendMessage);
            }
        } else {
            String text = """
                    Данная задача была отменена"
                    Название Задачи: """ + task.getTaskTitle();

            producerService.sendToOperatorDispatcherResponseSendMessage(createSendMessage(text, Long.valueOf(groupChatId)));
        }
    }

    @Override
    public void handleUpdate(Update update) {
        if (update.hasMessage() && !update.getMessage().getFrom().getIsBot()
                && (update.getMessage().hasText() || update.getMessage().hasVoice()
                || update.getMessage().hasPhoto() || update.getMessage().hasDocument())) {
            distributeMessageByType(update);
        } else if (update.hasCallbackQuery() && !update.getCallbackQuery().getFrom().getIsBot()) {
            distributeCallBackQueryByType(update);
        } else {
            log.warn("Got unsupported type of update: " + update);
        }
    }

    private void distributeCallBackQueryByType(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        var telegramUserId = update.getCallbackQuery().getFrom().getId();
        var userName = update.getCallbackQuery().getFrom().getUserName();

        String[] callBackData = update.getCallbackQuery().getData().split("\\|");

        ServiceButton serviceButton = ServiceButton.fromValue(callBackData[0]);

        String taskId = null;
        if (callBackData.length >= 2) {
            taskId = callBackData[1];
        }

        var operator = operatorCacheService.getOperator(userName, chatId, telegramUserId);

        if (serviceButton != null) {
            processServiceButton(serviceButton, update, operator, taskId);
        } else {
            messageSenderService.unsupportedMessageType(chatId);
        }
    }

    private void distributeMessageByType(Update update) {
        var text = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();
        var telegramUserId = update.getMessage().getFrom().getId();
        var serviceCommand = ServiceCommand.fromValue(text);
        var userName = update.getMessage().getFrom().getUserName();

        var operator = operatorCacheService.getOperator(userName, chatId, telegramUserId);

        if (serviceCommand != null) {
            processServiceCommand(serviceCommand, update, operator);
        } else if (operator.getBotState().equals(WAIT_FOR_REGISTRATION_CODE_STATE)) {
            SendMessage sendMessage = operatorInteractionService.processRegistrationCode(update, operator);

            producerService.sendToOperatorDispatcherResponseSendMessage(sendMessage);
        } else if (operator.getBotState().equals(WAIT_FOR_ANSWER_STATE)) {
            TaskAnswerMessage taskAnswerForAppUser = taskService.processAnswer(update, operator);

            SendMessage answerForOperator;
            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createMainMenu();
            if (taskAnswerForAppUser != null) {
                answerForOperator = createSendMessage("Ваш ответ был успешно сохранен и отправлен пользователю", chatId, inlineKeyboardMarkup);
                producerService.sendToTaskResponseAnswerTaskAnswerMessage(taskAnswerForAppUser);
            } else {
                answerForOperator = createSendMessage("Юзера создавшего эту задачу более нет в нашей базе данных, пожалуйста свяжитесь с Максимом", chatId, inlineKeyboardMarkup);
            }

            producerService.sendToOperatorDispatcherResponseSendMessage(answerForOperator);
        } else {
            messageSenderService.unsupportedMessageType(chatId);
        }
    }

    private void processServiceButton(ServiceButton serviceButton, Update update, Operator operator, String taskId) {

        switch (serviceButton) {
            case MAIN_MENU ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(operatorInteractionService.processMainMenuButton(update), update);
            case MY_TASKS ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(operatorInteractionService.processMyTasksButton(update), update);
            case AVAILABLE_TASKS ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(operatorInteractionService.processAvailableTasksButton(update), update);
            case MY_COMPLETED_TASKS ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(operatorInteractionService.processMyCompletedTasksButton(update, operator), update);
            case MY_TASK ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(operatorInteractionService.processMyTaskButton(update, Integer.valueOf(taskId)), update);
            case MY_COMPLETED_TASK ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(operatorInteractionService.processMyCompletedTaskButton(update, Integer.valueOf(taskId)), update);
            case AVAILABLE_TASK ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(operatorInteractionService.processAvailableTaskButton(update, Integer.valueOf(taskId)), update);
            case INFOS ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(taskService.processInfosButton(update, Integer.valueOf(taskId)), update);
            case INFO ->
                    messageSenderService.sendBotResponseToOperatorDispatcher(taskService.processInfoButton(update, Long.valueOf(taskId)), update);
            case ACCEPT_TASK ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(taskService.processAcceptTaskButton(update, Integer.valueOf(taskId)), update);
            case GIVE_ANSWER ->
                    messageSenderService.sendToOperatorDispatcherResponseEditMessageText(taskService.processGiveAnswerButton(update, Integer.valueOf(taskId), operator), update);
        }
    }

    private void processServiceCommand(ServiceCommand serviceCommand, Update update, Operator operator) {
        switch (serviceCommand) {
            case REGISTRATION ->
                    producerService.sendToOperatorDispatcherResponseSendMessage(operatorInteractionService.handleRegistrationCommand(update, operator));
            case MAIN_MENU ->
                    producerService.sendToOperatorDispatcherResponseSendMessage(operatorInteractionService.handleMainMenuCommand(update, operator));
        }
    }
}