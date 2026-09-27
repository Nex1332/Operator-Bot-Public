package de.operator.bot.service.impl;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import de.operator.bot.service.StripeEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StripeEventServiceImpl implements StripeEventService {
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Override
    public Event processEvent(String payload, String sigHeader) throws SignatureVerificationException {
        return Webhook.constructEvent(payload, sigHeader, endpointSecret);
    }

    @Override
    public Session processStripeObject(Event event) {
        Optional<StripeObject> stripeObjectOptional = event.getDataObjectDeserializer().getObject();
        return (Session) stripeObjectOptional.orElse(null);
    }
}
