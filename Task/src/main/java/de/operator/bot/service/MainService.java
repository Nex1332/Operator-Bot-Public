package de.operator.bot.service;

import de.operator.bot.PaymentStatus;
import de.operator.bot.ProcessTaskRequest;
import de.operator.bot.TaskAnswerMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface MainService {
    void handlePaymentStatus(PaymentStatus paymentStatus);

    void handleProcessTaskRequest(ProcessTaskRequest processTaskRequest);

    void handleRequestCancelTask(Integer taskId);

    void handleProcessTaskAnswerMessage(TaskAnswerMessage taskAnswerMessage);
}
