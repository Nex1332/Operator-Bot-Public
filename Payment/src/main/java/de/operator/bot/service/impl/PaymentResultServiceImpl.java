package de.operator.bot.service.impl;

import de.operator.bot.PaymentFeedback;
import de.operator.bot.PaymentStatus;
import de.operator.bot.service.PaymentResultService;
import de.operator.bot.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Log4j
@Service
@RequiredArgsConstructor
public class PaymentResultServiceImpl implements PaymentResultService {
    private final ProducerService producerService;

    @Override
    public void paymentConfirmed(String chatId, String messageId, String telegramUserId, String paymentIntent) {
        log.info("Sending Node Payment Feedback (Payment confirmed)");
        producerService.sendToNodePaymentFeedbackOrResponseForCreatingLinkRequest(PaymentFeedback.builder()
                .chatId(Long.valueOf(chatId))
                .messageId(Integer.valueOf(messageId))
                .text("""
                        Оплата прошла успешно! Через 30 минут вы получите подробную информацию о вашем термине.
                        Вы можете изменить или отменить заказ в разделе «Мои заказы».
                        Если возникнут какие-либо ошибки, вы можете сообщить о них.
                        Также вы можете запросить возврат средств, если наши услуги не оправдают ваших ожиданий.
                        """)
                .build());

        log.info("Sending task payment status (Payment confirmed)");
        producerService.sendToTaskPaymentStatus(PaymentStatus.builder()
                .telegramUserId(Long.valueOf(telegramUserId))
                .isPaid(true)
                .paymentIntent(paymentIntent)
                .build());
    }

    @Override
    public void paymentFailed(String chatId, String messageId, String telegramUserId) {
        log.info("Sending Node payment status (Payment failed)");
        producerService.sendToNodePaymentFeedbackOrResponseForCreatingLinkRequest(PaymentFeedback.builder()
                .chatId(Long.valueOf(chatId))
                .messageId(Integer.valueOf(messageId))
                .text("Простите, но что-то пошло не так при оплате")
                .build());

        log.info("Sending task payment status (Payment failed)");
        producerService.sendToTaskPaymentStatus(PaymentStatus.builder()
                .telegramUserId(Long.valueOf(telegramUserId))
                .isPaid(false)
                .build());
    }
}
