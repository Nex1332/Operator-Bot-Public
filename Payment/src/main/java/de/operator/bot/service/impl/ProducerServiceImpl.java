package de.operator.bot.service.impl;

import de.operator.bot.PaymentFeedback;
import de.operator.bot.PaymentStatus;
import de.operator.bot.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    @Value("${spring.rabbitmq.queues.payment_node_payment-feedback-queue}")
    private String paymentNodePaymentFeedbackQueue;
    @Value("${spring.rabbitmq.queues.payment_task_payment-status-queue}")
    private String paymentTaskPaymentStatusQueue;
    @Value("${spring.rabbitmq.queues.payment_operator_hot-exception-queue}")
    private String paymentOperatorHotExceptionQueue;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendToTaskPaymentStatus(PaymentStatus paymentStatus) {
        log.debug("Sending to Task PaymentStatus");
        rabbitTemplate.convertAndSend(paymentTaskPaymentStatusQueue, paymentStatus);
    }

    @Override
    public void sendToNodePaymentFeedbackOrResponseForCreatingLinkRequest(PaymentFeedback paymentFeedback) {
        log.debug("Sending to Node PaymentFeedback");
        rabbitTemplate.convertAndSend(paymentNodePaymentFeedbackQueue, paymentFeedback);
    }

    @Override
    public void sendToOperatorHotException(String exception) {
        log.debug("Sending to Operator hot Exception");
        rabbitTemplate.convertAndSend(paymentOperatorHotExceptionQueue, exception);
    }
}
