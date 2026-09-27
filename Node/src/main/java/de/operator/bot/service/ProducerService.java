package de.operator.bot.service;

import de.operator.bot.CreatePaymentLinkRequest;
import de.operator.bot.PaymentStatus;
import de.operator.bot.ProcessTaskRequest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

public interface ProducerService {
    void sendToDispatcherResponseSendMessage(SendMessage sendMessage);
    void sendToDispatcherResponseEditMessageText(EditMessageText editMessageText);
    void sendToPaymentRequestCreatePaymentLink(CreatePaymentLinkRequest createPaymentLinkRequest);
    void sendToTaskRequestProcessTask(ProcessTaskRequest processTaskRequest);
    void sendToTaskRequestCancelTask(Integer currentTaskId);
    void sendToTaskPaymentStatus(PaymentStatus freeOrder);
}
