package de.operator.bot.service;

import de.operator.bot.PaymentFeedback;
import de.operator.bot.entity.AppUser;
import de.operator.bot.entity.enums.TaskType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UserInteractionService {
    void handleStartCommand(Update update);

    void handleChoseLanguageButton(Update update, AppUser appUser, String languageCode);

    void handleOperatorsCommand(Update update);

    void handleMakeTerminButton(Update update);

    void handleMakeDocumentOrderButton(Update update, AppUser appUser);

    void handleConfirmAndPayButton(Update update, AppUser appUser);

    void handleMakeOrderButton(Update update, AppUser appUser, TaskType taskType);

    void handleBackToMenuButton(Update update, AppUser appUser);

    void handleFreeOrderButton(Update update, AppUser appUser);

    void handlePaymentFeedback(PaymentFeedback paymentFeedback);

    void handleEmptyUserName(Update update);

    void handleTaskAnswerMessage(SendMessage sendMessage);

    void handleAcceptCompletedTask(Update update, AppUser appUser);

}