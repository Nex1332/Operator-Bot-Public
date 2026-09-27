package de.operator.bot.service.impl;

import de.operator.bot.entity.Task;
import de.operator.bot.service.InlineKeyboardService;
import de.operator.bot.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static de.operator.bot.entity.enums.TaskType.*;
import static de.operator.bot.service.enums.ServiceButton.*;

@RequiredArgsConstructor
@Service
public class InlineKeyboardServiceImpl implements InlineKeyboardService {
    private final TranslationService translationService;

    @Override
    public InlineKeyboardMarkup createChoseLanguageMenu(){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 =
                List.of(button("Russian(Русский)", "ru"),
                        button("German(Deutch)",  "de"));

        List<InlineKeyboardButton> row2 =
                List.of(button("English",  "en"),
                        button("Ukrainian(Українська)",  "uk"));

        rows.add(row1);
        rows.add(row2);
        return setRows(rows);
    }

    @Override
    public InlineKeyboardMarkup createMainMenu(String languageCode) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 =
                List.of(button(translationService
                                .translate("main.menu.my.orders", new Locale(languageCode)), ALL_MY_ORDERS.toString()),
                        button(translationService
                                .translate("main.menu.make.termin", new Locale(languageCode)), MAKE_TERMIN.toString()));

        List<InlineKeyboardButton> row2 =
                List.of(button(translationService
                                .translate("main.menu.make.letter.assistance.order", new Locale(languageCode)), MAKE_ORDER + "|" + MAKE_LETTER_ASSISTANCE_ORDER),
                        button(translationService
                                .translate("main.menu.make.translator.order", new Locale(languageCode)), MAKE_ORDER + "|" + MAKE_TRANSLATOR_ORDER));

        List<InlineKeyboardButton> row3 =
                List.of(button(translationService
                                .translate("main.menu.make.document.order", new Locale(languageCode)), MAKE_DOCUMENT_ORDER.toString()),
                        button(translationService
                                .translate("main.menu.make.other.order", new Locale(languageCode)), MAKE_ORDER + "|" + OTHER_ORDER));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        return setRows(rows);
    }

    @Override
    public InlineKeyboardMarkup createChoseTerminTypeMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 =
                List.of(button(translationService
                                .translate("chose.termin.type.menu.doctor", locale), MAKE_ORDER + "|" + DOCTOR_TERMIN),
                        button(translationService
                                .translate("chose.termin.type.menu.job.center", locale), MAKE_ORDER + "|" + JOB_CENTER_TERMIN));

        List<InlineKeyboardButton> row2 =
                List.of(button(translationService
                                .translate("chose.termin.type.menu.bank", locale), MAKE_ORDER + "|" + BANK_TERMIN),
                        button(translationService
                                .translate("chose.termin.type.menu.other", locale), MAKE_ORDER + "|" + OTHER_ORDER_TERMIN));

        rows.add(row1);
        rows.add(row2);

        return setRows(addBackToMenuButton(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createChoseDocumentTypeMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 =
                List.of(button(translationService
                                .translate("chose.document.type.menu.kindergeld", locale), MAKE_ORDER + "|" + KINDERGELD_DOCUMENT),
                        button(translationService
                                .translate("chose.document.type.menu.job.center", locale), MAKE_ORDER + "|" + JOB_CENTER_DOCUMENT));

        List<InlineKeyboardButton> row2 =
                List.of(button(translationService
                                .translate("chose.document.type.menu.car.registration", locale), MAKE_ORDER + "|" + CAR_REGISTRATION_DOCUMENT),
                        button(translationService
                                .translate("chose.document.type.menu.house.registration", locale), MAKE_ORDER + "|" + HOUSE_REGISTRATION_DOCUMENT));

        List<InlineKeyboardButton> row3 =
                List.of(button(translationService
                                .translate("chose.document.type.menu.bank", locale), MAKE_ORDER + "|" + BANK_DOCUMENT),
                        button(translationService
                                .translate("chose.document.type.menu.other", locale), MAKE_ORDER + "|" + OTHER_ORDER_DOCUMENT));

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        return setRows(addBackToMenuButton(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createConfirmAndPayMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(button(translationService
                .translate("confirm.and.pay.menu.pay", locale), PAY.toString())));
        rows.add(List.of(button(translationService
                .translate("confirm.and.pay.menu.use.free.order", locale), USE_FREE_ORDER.toString())));

        return setRows(addBackToMenuButton(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createTaskAnswerMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(button(translationService
                .translate("task.answer.menu.accept.completed.task", locale), ACCEPT_COMPLETED_TASK.toString())));
        rows.add(List.of(button(translationService
                .translate("task.answer.menu.report.a.problem", locale), REPORT_A_PROBLEM.toString())));

        return setRows(addBackToMenuButton(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createNoFreeOrdersMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(button(translationService
                .translate("no.free.orders.menu.pay", locale), ACCEPT_COMPLETED_TASK.toString())));

        return setRows(addBackToMenuButton(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createAllMyOrdersMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(button(translationService
                .translate("all.my.orders.menu.confirmed.orders", locale), CONFIRMED_ORDERS.toString())));
        rows.add(List.of(button(translationService
                .translate("all.my.orders.menu.unconfirmed.orders", locale), UNCONFIRMED_ORDERS.toString())));

        return setRows(addBackToMenuButton(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createMySpecificOrdersMenu(List<Task> tasks, Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (Task task : tasks) {
            rows.add(List.of(button(task.getTaskTitle(), String.valueOf(task.getTaskId()))));
        }

        return setRows(addBackToMyOrdersAndMainMenuButtons(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createMyConfirmedOrderSettingsMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(button(translationService
                .translate("my.confirmed.order.settings.menu.report.a.problem", locale), REPORT_A_PROBLEM.toString())));

        return setRows(addBackToMyOrdersAndMainMenuButtons(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createMyUnconfirmedOrderSettingsMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(button(translationService
                .translate("my.unconfirmed.order.settings.menu.add.of.change.info", locale), ADD_OR_CHANGE_INFO.toString())));
        rows.add(List.of(button(translationService
                .translate("my.unconfirmed.order.settings.menu.cancel.order", locale), CANCEL_ORDER.toString())));

        return setRows(addBackToMyOrdersAndMainMenuButtons(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createCancelMyOrderMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(button(translationService
                .translate("cancel.my.order.menu.request.refund", locale), REQUEST_REFUND.toString())));
        rows.add(List.of(button(translationService
                .translate("cancel.my.order.menu.request.free.order", locale), REQUEST_FREE_ORDER.toString())));

        return setRows(addBackToMyOrdersAndMainMenuButtons(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createRequestRefundMenu(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(button(translationService
                .translate("request.refund.menu.accept.request.refund", locale), ACCEPT_REQUEST_REFUND.toString())));
        rows.add(List.of(button(translationService
                .translate("request.refund.menu.request.free.order", locale), REQUEST_FREE_ORDER.toString())));

        return setRows(addBackToMyOrdersAndMainMenuButtons(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createBackToMenuOrBackToMyOrderInlineKeyboardMarkup(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        return setRows(addBackToMyOrdersAndMainMenuButtons(rows, locale));
    }

    @Override
    public InlineKeyboardMarkup createBackToMenuInlineKeyboardMarkup(Locale locale) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(button(translationService
                .translate("back.to.menu.button", locale), BACK_TO_MENU.toString())));

        return setRows(rows);
    }

    @Override
    public InlineKeyboardMarkup createAcceptUserAgremmentInlineKeyboardMarkup(String languageCode) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(button(translationService
                .translate("accept.user.agreement.continue", new Locale(languageCode)), BACK_TO_MENU.toString())));

        return setRows(rows);
    }


    private List<List<InlineKeyboardButton>> addBackToMyOrdersAndMainMenuButtons(List<List<InlineKeyboardButton>> rows, Locale locale) {
        rows.add(List.of(button(translationService
                .translate("back.to.my.orders", locale), ALL_MY_ORDERS.toString())));
        rows.add(List.of(button(translationService
                .translate("main.menu", locale), BACK_TO_MENU.toString())));

        return rows;
    }

    private List<List<InlineKeyboardButton>> addBackToMenuButton(List<List<InlineKeyboardButton>> rows, Locale locale) {
        rows.add(List.of(button(translationService
                .translate("main.menu", locale), BACK_TO_MENU.toString())));

        return rows;
    }

    private InlineKeyboardButton button(String text, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    private static InlineKeyboardMarkup setRows(List<List<InlineKeyboardButton>> rows) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
    }
}