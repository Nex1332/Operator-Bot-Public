package de.operator.bot.service.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import de.operator.bot.CreatePaymentLinkRequest;
import de.operator.bot.PaymentFeedback;
import de.operator.bot.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Log4j
@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {
    private final PaymentService paymentService;
    private final StripeEventService stripeEventService;
    private final ProducerService producerService;
    private final PaymentResultService paymentResultService;

    @Override
    public void processCreatePaymentLinkRequest(CreatePaymentLinkRequest createPaymentLinkRequest) {
        try {
            String paymentUrl = paymentService.createCheckoutSession(createPaymentLinkRequest);

            producerService.sendToNodePaymentFeedbackOrResponseForCreatingLinkRequest(PaymentFeedback.builder()
                    .chatId(createPaymentLinkRequest.getChatId())
                    .messageId(createPaymentLinkRequest.getMessageId())
                    .text(paymentUrl)
                    .build());
        } catch (StripeException e) {
            log.error("Something went wrong. While an attempt of creating of the CheckoutSession for User with chatId " + createPaymentLinkRequest.getChatId() + " Error: " + e);

            String exception = """
                    Простите, что-то пошло не так при создании ссылки для оплаты. 
                    Пожалуйста, свяжитесь с нашим оператором напрямую для оформления заказа. 
                    Для связи с оператором введите команду /support. 
                    Спасибо за ваше понимание.
                                        
                    """;

            producerService.sendToNodePaymentFeedbackOrResponseForCreatingLinkRequest(PaymentFeedback.builder()
                    .chatId(createPaymentLinkRequest.getChatId())
                    .messageId(createPaymentLinkRequest.getMessageId())
                    .text(exception)
                    .build());
        }
    }

    @Override
    public void processStripeEvent(String payload, String sigHeader) throws SignatureVerificationException {
        Event event = stripeEventService.processEvent(payload, sigHeader);

        Session session = stripeEventService.processStripeObject(event);
        if (session != null) {
            String chatId = session.getMetadata().get("chatId");
            String messageId = session.getMetadata().get("messageId");
            String telegramUserId = session.getMetadata().get("telegramUserId");
            String paymentIntent = session.getPaymentIntent();

            switch (event.getType()) {
                case "checkout.session.completed" -> paymentResultService.paymentConfirmed(chatId, messageId, telegramUserId, paymentIntent);
                case "checkout.session.expired", "payment_intent.payment_failed" -> paymentResultService.paymentFailed(chatId, messageId, telegramUserId);
                default -> {
                    log.info("Unsupported event type " + event.getType() + " in the Session with Id"
                            + event.getId() + " by user with chatId " + chatId);
                    paymentResultService.paymentFailed(chatId, messageId, telegramUserId);
                }
            }
        } else {
            String exception = "Stripe session is Null";
            log.error(exception);

            producerService.sendToOperatorHotException(exception);
        }
    }
}
