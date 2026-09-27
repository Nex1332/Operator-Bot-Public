package de.operator.bot.service.impl;

import de.operator.bot.CreatePaymentLinkRequest;
import de.operator.bot.service.ConsumerService;
import de.operator.bot.service.MainService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    @Timed
    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.node_payment_request-create-payment-link-queue}")
    public void consumeFromNodeRequestCreateLink(CreatePaymentLinkRequest createPaymentLinkRequest) {
        log.debug("Consuming from Node Request Create Link CreatePaymentLinkRequest");
        mainService.processCreatePaymentLinkRequest(createPaymentLinkRequest);
    }
}
