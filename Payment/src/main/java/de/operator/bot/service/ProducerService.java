package de.operator.bot.service;

import de.operator.bot.PaymentFeedback;
import de.operator.bot.PaymentStatus;

public interface ProducerService {
    void sendToTaskPaymentStatus(PaymentStatus paymentStatus);
    void sendToNodePaymentFeedbackOrResponseForCreatingLinkRequest(PaymentFeedback paymentFeedback);
    void sendToOperatorHotException(String exception);
}
