package de.operator.bot.service;

import com.stripe.exception.StripeException;
import de.operator.bot.CreatePaymentLinkRequest;

public interface PaymentService {
    String createCheckoutSession(CreatePaymentLinkRequest createPaymentLinkRequest) throws StripeException;
}
