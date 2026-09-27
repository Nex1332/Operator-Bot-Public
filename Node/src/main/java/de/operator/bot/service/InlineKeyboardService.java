package de.operator.bot.service;

import de.operator.bot.entity.Task;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Locale;

public interface InlineKeyboardService {

    InlineKeyboardMarkup createChoseLanguageMenu();

    InlineKeyboardMarkup createMainMenu(String languageCode);

    InlineKeyboardMarkup createNoFreeOrdersMenu(Locale locale);

    InlineKeyboardMarkup createAllMyOrdersMenu(Locale locale);

    InlineKeyboardMarkup createMySpecificOrdersMenu(List<Task> tasks, Locale locale);

    InlineKeyboardMarkup createMyConfirmedOrderSettingsMenu(Locale locale);

    InlineKeyboardMarkup createMyUnconfirmedOrderSettingsMenu(Locale locale);

    InlineKeyboardMarkup createChoseTerminTypeMenu(Locale locale);

    InlineKeyboardMarkup createChoseDocumentTypeMenu(Locale locale);

    InlineKeyboardMarkup createCancelMyOrderMenu(Locale locale);

    InlineKeyboardMarkup createRequestRefundMenu(Locale locale);

    InlineKeyboardMarkup createConfirmAndPayMenu(Locale locale);

    InlineKeyboardMarkup createTaskAnswerMenu(Locale locale);

    InlineKeyboardMarkup createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(Locale locale);

    InlineKeyboardMarkup createAcceptUserAgremmentInlineKeyboardMarkup(String languageCode);

    InlineKeyboardMarkup createBackToMenuInlineKeyboardMarkup(Locale locale);
}