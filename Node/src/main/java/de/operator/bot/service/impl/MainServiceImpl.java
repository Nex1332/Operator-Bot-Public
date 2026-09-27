package de.operator.bot.service.impl;

import de.operator.bot.TaskProcessingErrorEvent;
import de.operator.bot.cache.AppUserCacheService;
import de.operator.bot.entity.AppUser;
import de.operator.bot.entity.enums.TaskType;
import de.operator.bot.enums.ErrorType;
import de.operator.bot.service.*;
import de.operator.bot.service.enums.ServiceButton;
import de.operator.bot.service.enums.ServiceCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

import static de.operator.bot.entity.enums.BotState.*;
import static de.operator.bot.enums.ErrorType.*;

@Log4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final MessageSenderService messageSenderService;
    private final UserInteractionService userInteractionService;
    private final SimpleTaskService simpleTaskService;
    private final MyOrdersService myOrdersService;
    private final AppUserCacheService appUserCacheService;

    @Override
    public void handleUpdate(Update update) {
        if (update.hasMessage()
                && (update.getMessage().hasText() || update.getMessage().hasVoice()
                || update.getMessage().hasPhoto() || update.getMessage().hasDocument())) {
            distributeMessageByType(update);
        } else if (update.hasCallbackQuery()) {
            distributeCallBackQueryByType(update);
        } else {
            log.warn("Got unsupported type of update: " + update);
        }
    }

    @Override
    public void handleTaskErrorEvent(TaskProcessingErrorEvent taskProcessingErrorEvent) {
        ErrorType errorType = taskProcessingErrorEvent.getErrorType();
        Long chatId = taskProcessingErrorEvent.getChatId();
        AppUser appUser = appUserCacheService.getAppUserByChatId(chatId);

        String languageCode;
        if (appUser != null) {
            languageCode = appUser.getLanguageCode();

            if (errorType.equals(TOO_MUCH_INFOS)) {
                simpleTaskService.processErrorTooMuchInfos(chatId, new Locale(languageCode));
            } else {
                simpleTaskService.processTaskPaymentTimeout(chatId, new Locale(languageCode));
            }
        } else {
            log.error("AppUser in cache is null, chatId:" + chatId);
        }
    }

    private void distributeMessageByType(Update update) {
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();
        ServiceCommand serviceCommand = ServiceCommand.fromValue(text);
        String userName = update.getMessage().getFrom().getUserName();

        AppUser appUser = appUserCacheService.getAppUser(userName, chatId, telegramUserId);

        if (serviceCommand != null) {
            processServiceCommand(serviceCommand, update);
        } else if (appUser.getBotState().equals(WAIT_FOR_TASK_TITLE_STATE) && update.getMessage().hasText()) {
            simpleTaskService.handleTaskTitle(update, appUser);
        } else if (appUser.getBotState().equals(WAIT_FOR_INFO_STATE) || appUser.getBotState().equals(WAIT_FOR_ADDITIONAL_INFO_STATE)) {
            simpleTaskService.handleNewInfo(update, appUser);
        } else {
            messageSenderService.unsupportedMessageType(chatId, appUser);
        }
    }

    private void distributeCallBackQueryByType(Update update) {
        String[] callBackData = update.getCallbackQuery().getData().split("\\|");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramUserId = update.getCallbackQuery().getFrom().getId();
        String userName = update.getCallbackQuery().getFrom().getUserName();

        AppUser appUser = appUserCacheService.getAppUser(userName, chatId, telegramUserId);

        ServiceButton serviceButton = ServiceButton.fromValue(callBackData[0]);

        TaskType taskType = null;
        if (callBackData.length >= 2) {
            taskType = TaskType.valueOf(callBackData[1]);
        }

        if (serviceButton != null) {
            processServiceButton(serviceButton, update, appUser, taskType);
        } else if (appUser.getBotState().equals(WAIT_FOR_MY_ORDER_STATE)) {
            myOrdersService.handleMyOrderSettings(update, appUser);
        } else if (appUser.getBotState().equals(WAIT_FOR_LANGUAGE_STATE)) {
            userInteractionService.handleChoseLanguageButton(update, appUser, update.getCallbackQuery().getData());
        } else {
            messageSenderService.unsupportedMessageType(chatId, appUser);
        }
    }

    private void processServiceButton(ServiceButton serviceButton, Update update, AppUser appUser, TaskType taskType) {
        switch (serviceButton) {
            case PAY -> userInteractionService.handleConfirmAndPayButton(update, appUser);
            case MAKE_ORDER -> userInteractionService.handleMakeOrderButton(update, appUser, taskType);
            case MAKE_TERMIN -> userInteractionService.handleMakeTerminButton(update);
            case MAKE_DOCUMENT_ORDER -> userInteractionService.handleMakeDocumentOrderButton(update, appUser);
            case ALL_MY_ORDERS -> myOrdersService.handleAllMyOrdersButton(update, appUser);
            case CONFIRMED_ORDERS, UNCONFIRMED_ORDERS -> myOrdersService.handleMySpecificOrdersButton(update, appUser, serviceButton);
            case ADD_OR_CHANGE_INFO -> myOrdersService.handleAddInformationButton(update, appUser);
            case CANCEL_ORDER -> myOrdersService.handleCancelMyOrderButton(update, appUser);
            case REPORT_A_PROBLEM -> myOrdersService.handelReportProblemButton(update, appUser);
            case ACCEPT_COMPLETED_TASK -> userInteractionService.handleAcceptCompletedTask(update, appUser);
            case REQUEST_FREE_ORDER -> myOrdersService.handleRequestFreeOrderButton(update, appUser);
            case REQUEST_REFUND -> myOrdersService.handleRequestRefundButton(update, appUser);
            case ACCEPT_REQUEST_REFUND -> myOrdersService.handleAcceptRequestRefundButton(update, appUser);
            case USE_FREE_ORDER -> userInteractionService.handleFreeOrderButton(update, appUser);
            case BACK_TO_MENU -> userInteractionService.handleBackToMenuButton(update, appUser);
        }
    }

    private void processServiceCommand(ServiceCommand serviceCommand, Update update) {
        switch (serviceCommand) {
            case START -> userInteractionService.handleStartCommand(update);
            case OPERATORS -> userInteractionService.handleOperatorsCommand(update);
        }
    }
}
