package de.operator.bot.configuration;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class StripeConfiguration {
    @Value("${stripe.api-key}")
    private String stripeApiKey;

    @PostConstruct
    private void init() {
        Stripe.apiKey = stripeApiKey;
    }
}
