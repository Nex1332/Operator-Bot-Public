package de.operator.bot.service;

import de.operator.bot.PaymentFeedback;
import de.operator.bot.TaskProcessingErrorEvent;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeFromDispatcherUserUpdate(Update update);
    void consumeFromPaymentPaymentFeedback(PaymentFeedback paymentFeedback);
    void consumeFromTaskResponseSendMessage(SendMessage sendMessage);
    void consumeFromTaskTaskErrorEvent(TaskProcessingErrorEvent taskProcessingErrorEvent);
}
