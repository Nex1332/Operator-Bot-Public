package de.operator.bot.service;

import de.operator.bot.entity.AppUser;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

public interface SimpleTaskService {

    void handleTaskTitle(Update update, AppUser appUser);

    void handleNewInfo(Update update, AppUser appUser);

    void processErrorTooMuchInfos(Long chatId, Locale locale);

    void processTaskPaymentTimeout(Long chatId, Locale locale);
}
