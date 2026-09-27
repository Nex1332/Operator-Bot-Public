package de.operator.bot.service.impl;

import de.operator.bot.cache.AppUserCacheService;
import de.operator.bot.cache.TaskCacheService;
import de.operator.bot.entity.AppUser;
import de.operator.bot.entity.Operator;
import de.operator.bot.entity.Task;
import de.operator.bot.repository.OperatorRepository;
import de.operator.bot.repository.TaskRepository;
import de.operator.bot.service.InlineKeyboardService;
import de.operator.bot.service.MyOrdersService;
import de.operator.bot.service.ProducerService;
import de.operator.bot.service.enums.ServiceButton;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Locale;

import static de.operator.bot.entity.enums.BotState.*;
import static de.operator.bot.service.enums.ServiceButton.CONFIRMED_ORDERS;

@Service
@RequiredArgsConstructor
public class MyOrdersServiceImpl implements MyOrdersService {
    private final TaskRepository taskRepository;
    private final MessageSenderService messageSenderService;
    private final InlineKeyboardService inlineKeyboardService;
    private final ProducerService producerService;
    private final AppUserCacheService appUserCacheService;
    private final TaskCacheService taskCacheService;
    private final OperatorRepository operatorRepository;

    @Override
    public void handleAllMyOrdersButton(Update update, AppUser appUser) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createAllMyOrdersMenu(new Locale(appUser.getLanguageCode()));

        appUser.setBotState(BASE_STATE);
        appUser.setCurrentTaskId(null);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        messageSenderService.createAndSendEditMessageTextToDispatcher("all.my.orders", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleMySpecificOrdersButton(Update update, AppUser appUser, ServiceButton serviceButton) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        List<Task> tasks = taskRepository.findAllByAppUserId(appUser.getTelegramUserId());

        if (tasks.isEmpty()) {
            tasksAreEmpty(chatId, messageId, new Locale(appUser.getLanguageCode()));
        } else {
            InlineKeyboardMarkup inlineKeyboardMarkup;

            if (serviceButton.equals(CONFIRMED_ORDERS)) {
                inlineKeyboardMarkup = inlineKeyboardService
                        .createMySpecificOrdersMenu(tasks.stream().filter(Task::isComplete).toList(), new Locale(appUser.getLanguageCode()));
            } else {
                inlineKeyboardMarkup = inlineKeyboardService
                        .createMySpecificOrdersMenu(tasks.stream().filter(task -> !task.isComplete()).toList(), new Locale(appUser.getLanguageCode()));
            }

            appUser.setBotState(WAIT_FOR_MY_ORDER_STATE);
            appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

            messageSenderService.createAndSendEditMessageTextToDispatcher("my.specific.orders", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
        }
    }

    @Async
    @Override
    public void handleMyOrderSettings(Update update, AppUser appUser) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Integer taskId = Integer.valueOf(update.getCallbackQuery().getData());

        Task task = taskRepository.findByTaskId(taskId);

        InlineKeyboardMarkup inlineKeyboardMarkup;
        if (task.isComplete()) {
            inlineKeyboardMarkup = inlineKeyboardService.createMyConfirmedOrderSettingsMenu(new Locale(appUser.getLanguageCode()));
        } else inlineKeyboardMarkup = inlineKeyboardService.createMyUnconfirmedOrderSettingsMenu(new Locale(appUser.getLanguageCode()));

        appUser.setCurrentTaskId(taskId);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        messageSenderService.createAndSendEditMessageTextToDispatcher("my.order.settings", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleCancelMyOrderButton(Update update, AppUser appUser) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createCancelMyOrderMenu(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendEditMessageTextToDispatcher("cancel.my.order", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleRequestFreeOrderButton(Update update, AppUser appUser) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        producerService.sendToTaskRequestCancelTask(appUser.getCurrentTaskId());

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        appUser.setCurrentTaskId(null);

        messageSenderService.createAndSendEditMessageTextToDispatcher("request.free.order", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);

        appUser.setFreeOrder(appUser.getFreeOrder() + 1);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());
    }

    @Override
    public void handleRequestRefundButton(Update update, AppUser appUser) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        producerService.sendToTaskRequestCancelTask(appUser.getCurrentTaskId());

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createRequestRefundMenu(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendEditMessageTextToDispatcher("request.refund", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleAcceptRequestRefundButton(Update update, AppUser appUser) {
//        producerService.sentToPaymentRequestRefund();

        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        appUser.setCurrentTaskId(null);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        messageSenderService.createAndSendEditMessageTextToDispatcher("accept.request.refund", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleAddInformationButton(Update update, AppUser appUser) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        appUser.setBotState(WAIT_FOR_ADDITIONAL_INFO_STATE);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        Task task = taskRepository.findByTaskId(appUser.getCurrentTaskId());
        taskCacheService.putTask(task, appUser.getTelegramUserId());

        messageSenderService.createAndSendEditMessageTextToDispatcher("add.information", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handelReportProblemButton(Update update, AppUser appUser) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        Task task = taskRepository.findByTaskId(appUser.getCurrentTaskId());

        Operator operator = operatorRepository.findOperatorByTelegramUserId(task.getOperatorId());

        appUser.setCurrentTaskId(null);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        messageSenderService.createAndSendEditMessageTextToDispatcher(
               "report.problem", new Locale(appUser.getLanguageCode(), operator.getUsername()), chatId, messageId, inlineKeyboardMarkup);
    }

    private void tasksAreEmpty(Long chatId, Integer messageId, Locale locale) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(locale);

        messageSenderService.createAndSendEditMessageTextToDispatcher("tasks.are.empty", locale, chatId, messageId, inlineKeyboardMarkup);
    }
}
