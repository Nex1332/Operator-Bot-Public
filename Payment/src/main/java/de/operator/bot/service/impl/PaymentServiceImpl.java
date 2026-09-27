package de.operator.bot.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import de.operator.bot.CreatePaymentLinkRequest;
import de.operator.bot.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    @Value("${stripe.url.success}")
    private String successUrl;
    @Value("${stripe.url.cancel}")
    private String cancelUrl;

    @Override
    public String createCheckoutSession(CreatePaymentLinkRequest createPaymentLinkRequest) throws StripeException {
        Long chatId = createPaymentLinkRequest.getChatId();
        Integer messageId = createPaymentLinkRequest.getMessageId();
        Long telegramUserId = createPaymentLinkRequest.getTelegramUserId();

        SessionCreateParams sessionParams = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("eur")
                                        .setUnitAmount(200L)
                                        .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                .setName("Запись На Термин")
                                                .build())
                                        .build())
                                .build()
                )
                .putMetadata("chatId", String.valueOf(chatId))
                .putMetadata("messageId", String.valueOf(messageId))
                .putMetadata("telegramUserId", String.valueOf(telegramUserId))
                .build();

        return Session.create(sessionParams).getUrl();
    }

    public void createRefund() throws StripeException {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent("pi_1234567890abcdef")
                .setAmount(180L)
                .build();

        Refund refund = Refund.create(params);
    }
}

