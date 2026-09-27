package de.operator.bot.service;

public interface PaymentResultService {
    void paymentConfirmed(String chatId, String messageId, String telegramUserId, String paymentIntent);
    void paymentFailed(String chatId, String messageId, String telegramUserId);
}
