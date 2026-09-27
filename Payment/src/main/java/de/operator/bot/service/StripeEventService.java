package de.operator.bot.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;

public interface StripeEventService {
    Event processEvent(String payload, String sigHeader) throws SignatureVerificationException;
    Session processStripeObject(Event event);
}
