package de.operator.bot.service.impl;

import de.operator.bot.CreatePaymentLinkRequest;
import de.operator.bot.PaymentFeedback;
import de.operator.bot.PaymentStatus;
import de.operator.bot.cache.AppUserCacheService;
import de.operator.bot.cache.TaskCacheService;
import de.operator.bot.entity.AppUser;
import de.operator.bot.entity.Task;
import de.operator.bot.entity.enums.TaskType;
import de.operator.bot.repository.AppUserRepository;
import de.operator.bot.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.Locale;

import static de.operator.bot.entity.enums.BotState.*;

@Log4j
@Service
@RequiredArgsConstructor
public class UserInteractionServiceImpl implements UserInteractionService {
    private final InlineKeyboardService inlineKeyboardService;
    private final ProducerService producerService;
    private final MessageSenderService messageSenderService;
    private final AppUserCacheService appUserCacheService;
    private final AppUserRepository appUserRepository;
    private final TaskCacheService taskCacheService;
    private final OperatorsInfoService operatorsInfoService;
    @Value("${spring.text.user_agreement_url}")
    String userAgreementUrl;

    @Override
    public void handleStartCommand(Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createChoseLanguageMenu();
        String userName = update.getMessage().getFrom().getUserName();
        Long chatId = update.getMessage().getChatId();
        Long telegramUserId = update.getMessage().getFrom().getId();

        String text = """
                Please select the language that is most convenient for you.
                
                Bitte wählen Sie die Sprache, die für Sie am angenehmsten ist.
                
                Пожалуйста, выберите язык, который вам удобнее.
                
                Будь ласка, виберіть мову, яка вам зручніша.
                """;

        messageSenderService.createAndSendSendMessageToDispatcher(chatId, text, inlineKeyboardMarkup);

        AppUser appUser = appUserCacheService.getAppUser(userName, chatId, telegramUserId);
        appUser.setBotState(WAIT_FOR_LANGUAGE_STATE);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());
    }

    @Override
    public void handleChoseLanguageButton(Update update, AppUser appUser, String languageCode) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createAcceptUserAgremmentInlineKeyboardMarkup(languageCode);
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        appUser.setLanguageCode(languageCode);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        System.out.println(userAgreementUrl);
        messageSenderService.createAndSendEditMessageTextWithParseModeToDispatcher("chose.language", new Locale(appUser.getLanguageCode()), chatId, messageId, "HTML", inlineKeyboardMarkup, userAgreementUrl);
    }

    @Override
    public void handleOperatorsCommand(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        AppUser appUser = appUserRepository.findAppUserByChatId(chatId);
        String operators = operatorsInfoService.getAllOperatorsUserName().toString();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("operators.command", new Locale(appUser.getLanguageCode()), chatId, inlineKeyboardMarkup, operators);
    }

    @Override
    public void handleMakeTerminButton(Update update) {
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        AppUser appUser = appUserRepository.findAppUserByChatId(chatId);

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createChoseTerminTypeMenu(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendEditMessageTextToDispatcher("make.termin", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleMakeDocumentOrderButton(Update update, AppUser appUser) {
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createChoseDocumentTypeMenu(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendEditMessageTextToDispatcher("make.document.order", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleMakeOrderButton(Update update, AppUser appUser, TaskType taskType) {
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        appUser.setBotState(WAIT_FOR_TASK_TITLE_STATE);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        Task task = Task.builder()
                .taskId(update.getCallbackQuery().getMessage().getMessageId())
                .appUserId(update.getCallbackQuery().getFrom().getId())
                .isComplete(false)
                .isCanceled(false)
                .paid(false)
                .taskInfos(new ArrayList<>())
                .taskType(taskType)
                .build();

        taskCacheService.putTask(task, update.getCallbackQuery().getFrom().getId());

        messageSenderService.createAndSendEditMessageTextToDispatcher("make.order", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleBackToMenuButton(Update update, AppUser appUser) {
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createMainMenu(appUser.getLanguageCode());

        appUser.setBotState(BASE_STATE);
        appUser.setCurrentTaskId(null);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        messageSenderService.createAndSendEditMessageTextToDispatcher("back.to.menu", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }

    @Override
    public void handleConfirmAndPayButton(Update update, AppUser appUser) {
        Long appUserId = update.getCallbackQuery().getFrom().getId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Task task = taskCacheService.getTaskByAppUserId(appUserId);
        appUser.setBotState(BASE_STATE);
        appUserCacheService.putAppUser(appUser, appUserId);

        if (task != null) {
            taskCacheService.putTask(task, appUserId);

            producerService.sendToPaymentRequestCreatePaymentLink(CreatePaymentLinkRequest.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .telegramUserId(appUserId)
                    .build());
        } else {
            handleExperiencedConfirmTimeEvent(update);
        }
    }

    @Override
    public void handleFreeOrderButton(Update update, AppUser appUser) {
        Long appUserId = update.getCallbackQuery().getFrom().getId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();

        if (appUser.getFreeOrder() > 0) {
            producerService.sendToTaskPaymentStatus(PaymentStatus.builder()
                    .telegramUserId(appUserId)
                    .isPaid(true)
                    .paymentIntent("FREE_ORDER")
                    .build());

            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

            appUser.setFreeOrder(appUser.getFreeOrder() - 1);
            appUser.setBotState(BASE_STATE);
            appUserCacheService.putAppUser(appUser, appUserId);

            messageSenderService.createAndSendEditMessageTextToDispatcher("free.order", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
        } else {
            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createNoFreeOrdersMenu(new Locale(appUser.getLanguageCode()));

            messageSenderService.createAndSendEditMessageTextToDispatcher("free.order.error", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
        }
    }

    @Override
    public void handlePaymentFeedback(PaymentFeedback paymentFeedback) {
        Long chatId = paymentFeedback.getChatId();
        AppUser appUser = appUserRepository.findAppUserByChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendEditMessageTextToDispatcher(chatId, paymentFeedback.getMessageId(), paymentFeedback.getText(), inlineKeyboardMarkup);
    }

    @Override
    public void handleTaskAnswerMessage(SendMessage sendMessage) {
        Long chatId = Long.valueOf(sendMessage.getChatId());
        AppUser appUser = appUserRepository.findAppUserByChatId(chatId);
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createTaskAnswerMenu(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("task.answer.message", new Locale(appUser.getLanguageCode()), chatId, inlineKeyboardMarkup, sendMessage.getText());
    }

    @Override
    public void handleAcceptCompletedTask(Update update, AppUser appUser) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));
        Task task = taskCacheService.getTaskByAppUserId(appUser.getTelegramUserId());
        task.setComplete(true);
        taskCacheService.putTask(task, appUser.getTelegramUserId());

        messageSenderService.createAndSendEditMessageTextToDispatcher("accept.completed.task", new Locale(appUser.getLanguageCode()), update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId(), inlineKeyboardMarkup);
    }

    @Override
    public void handleEmptyUserName(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        AppUser appUser = appUserRepository.findAppUserByChatId(chatId);
        String operators = operatorsInfoService.getAllOperatorsUserName().toString();

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("empty.user.name", new Locale(appUser.getLanguageCode()), chatId, inlineKeyboardMarkup, operators);
    }

    private void handleExperiencedConfirmTimeEvent(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        AppUser appUser = appUserRepository.findAppUserByChatId(chatId);

        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        messageSenderService.createAndSendEditMessageTextToDispatcher("experienced.confirm.time.event", new Locale(appUser.getLanguageCode()), chatId, messageId, inlineKeyboardMarkup);
    }
}
