package de.operator.bot.service;

import de.operator.bot.PaymentStatus;
import de.operator.bot.entity.AppUser;
import de.operator.bot.entity.Task;
import de.operator.bot.entity.enums.FileType;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public interface TaskService {
    Task processPaymentStatus(PaymentStatus paymentStatus);

    void processInfoState(Update update, AppUser appUser, FileType fileType) throws TelegramApiException, IOException;

    Task processCancelTask(Integer taskId);
}
