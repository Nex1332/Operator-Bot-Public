package de.operator.bot.service.impl;

import de.operator.bot.ProcessTaskRequest;
import de.operator.bot.cache.AppUserCacheService;
import de.operator.bot.cache.TaskCacheService;
import de.operator.bot.entity.AppUser;
import de.operator.bot.entity.Task;
import de.operator.bot.entity.enums.BotState;
import de.operator.bot.entity.enums.FileType;
import de.operator.bot.service.InlineKeyboardService;
import de.operator.bot.service.ProducerService;
import de.operator.bot.service.SimpleTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Locale;

import static de.operator.bot.entity.enums.BotState.*;
import static de.operator.bot.entity.enums.FileType.*;


@Log4j
@Service
@RequiredArgsConstructor
public class SimpleTaskServiceImpl implements SimpleTaskService {
    private final AppUserCacheService appUserCacheService;
    private final TaskCacheService taskCacheService;
    private final InlineKeyboardService inlineKeyboardService;
    private final MessageSenderService messageSenderService;
    private final ProducerService producerService;
    @Value("${spring.normal-file-size}")
    int normalFileSize;

    @Override
    public void handleTaskTitle(Update update, AppUser appUser) {
        var chatId = update.getMessage().getChatId();
        var taskTitle = update.getMessage().getText();
        Long appUserId = update.getMessage().getFrom().getId();

        if (taskTitle.length() > 3 && taskTitle.length() < 50) {
            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));
            appUser.setBotState(WAIT_FOR_INFO_STATE);
            appUserCacheService.putAppUser(appUser, appUserId);

            Task task = taskCacheService.getTaskByAppUserId(appUserId);

            task.setTaskTitle(taskTitle);

            taskCacheService.putTask(task, appUserId);

            messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("task.title", new Locale(appUser.getLanguageCode()), chatId, inlineKeyboardMarkup);
        } else {
            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));
            messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("task.title.error", new Locale(appUser.getLanguageCode()), chatId, inlineKeyboardMarkup);
        }
    }

    @Override
    public void handleNewInfo(Update update, AppUser appUser) {
        Long chatId = appUser.getChatId();

        FileType fileType = getFileType(update);

        if (isInfoValid(update, fileType)) {
            producerService.sendToTaskRequestProcessTask(ProcessTaskRequest.builder()
                    .appUser(appUser)
                    .fileType(fileType.toString())
                    .update(update)
                    .build());

            BotState botState = appUser.getBotState();

            switch (botState) {
                case WAIT_FOR_INFO_STATE -> processInfo(chatId, new Locale(appUser.getLanguageCode()));
                case WAIT_FOR_ADDITIONAL_INFO_STATE -> processNewAdditionalInfo(appUser, chatId);
            }
        } else handelInvalidInfo(chatId, fileType, new Locale(appUser.getLanguageCode()));
    }

    private void processInfo(Long chatId, Locale locale) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createConfirmAndPayMenu(locale);

        messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("process.info", locale, chatId, inlineKeyboardMarkup);
    }

    private void processNewAdditionalInfo(AppUser appUser, Long chatId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(new Locale(appUser.getLanguageCode()));

        appUser.setBotState(BASE_STATE);
        appUser.setCurrentTaskId(null);
        appUserCacheService.putAppUser(appUser, appUser.getTelegramUserId());

        messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("process.new.additional.info", new Locale(appUser.getLanguageCode()), chatId, inlineKeyboardMarkup);
    }

    private void handelInvalidInfo(Long chatId, FileType fileType, Locale locale) {
        log.info("The message is too short or too long. Was sent by User with chatId " + chatId);

        String key;

        switch (fileType) {
            case TEXT -> key = "invalid.info.text";
            case VOICE -> key = "invalid.info.voice";
            default -> key = "invalid.info.default";
        }
        messageSenderService.createAndSendSendMessageToDispatcherWithTranslate(key, locale, chatId, null);
    }

    @Override
    public void processErrorTooMuchInfos(Long chatId, Locale locale) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(locale);

        messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("process.error.too.much.infos", locale, chatId, inlineKeyboardMarkup);
    }

    @Override
    public void processTaskPaymentTimeout(Long chatId, Locale locale) {
        InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(locale);

        messageSenderService.createAndSendSendMessageToDispatcherWithTranslate("process.task.payment.timeout", locale, chatId, inlineKeyboardMarkup);
    }

    private static FileType getFileType(Update update) {
        if (update.getMessage().hasText()) {
            return TEXT;
        } else if (update.getMessage().hasVoice()) {
            return VOICE;
        } else if (update.getMessage().hasPhoto()) {
            return PHOTO;
        } else {
            return DOCUMENT;
        }
    }

    private boolean isInfoValid(Update update, FileType fileType) {
        switch (fileType) {
            case TEXT -> {
                var taskText = update.getMessage().getText();
                return taskText.length() > 4;
            }
            case VOICE -> {
                Integer duration = update.getMessage().getVoice().getDuration();
                return duration > 10 && duration < 120;
            }
            case DOCUMENT -> {
                var documentSize = update.getMessage().getDocument().getFileSize();
                return documentSize < normalFileSize;
            }
            default -> {
                return true;
            }
        }
    }
}
