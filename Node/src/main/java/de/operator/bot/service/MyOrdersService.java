package de.operator.bot.service;

import de.operator.bot.service.enums.ServiceButton;
import org.telegram.telegrambots.meta.api.objects.Update;
import de.operator.bot.entity.AppUser;

public interface MyOrdersService {
    void handleAllMyOrdersButton(Update update, AppUser appUser);

    void handleMySpecificOrdersButton(Update update, AppUser appUser, ServiceButton serviceButton);

    void handleMyOrderSettings(Update update, AppUser appUser);

    void handleCancelMyOrderButton(Update update, AppUser appUser);

    void handleAddInformationButton(Update update, AppUser appUser);

    void handelReportProblemButton(Update update, AppUser appUser);

    void handleRequestFreeOrderButton(Update update, AppUser appUser);

    void handleRequestRefundButton(Update update, AppUser appUser);

    void handleAcceptRequestRefundButton(Update update, AppUser appUser);
}
