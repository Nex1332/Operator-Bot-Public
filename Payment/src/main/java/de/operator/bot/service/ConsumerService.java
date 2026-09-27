package de.operator.bot.service;

import de.operator.bot.CreatePaymentLinkRequest;

public interface ConsumerService {
    void consumeFromNodeRequestCreateLink(CreatePaymentLinkRequest createPaymentLinkRequest);
}
