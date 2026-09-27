package de.operator.bot.service;

import com.stripe.exception.SignatureVerificationException;
import de.operator.bot.CreatePaymentLinkRequest;

public interface MainService {
    void processCreatePaymentLinkRequest(CreatePaymentLinkRequest createPaymentLinkRequest);
    void processStripeEvent(String payload, String sigHeader) throws SignatureVerificationException;
}
