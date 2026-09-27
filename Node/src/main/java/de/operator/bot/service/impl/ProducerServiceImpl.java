package de.operator.bot.service.impl;

import de.operator.bot.CreatePaymentLinkRequest;
import de.operator.bot.PaymentStatus;
import de.operator.bot.ProcessTaskRequest;
import de.operator.bot.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    @Value("${spring.rabbitmq.queues.node_dispatcher_response-send-message-queue}")
    private String nodeDispatcherResponseSendMessageQueue;
    @Value("${spring.rabbitmq.queues.node_dispatcher_response-edit-message-text-queue}")
    private String nodeDispatcherResponseEditMessageTextQueue;
    @Value("${spring.rabbitmq.queues.node_payment_request-create-payment-link-queue}")
    private String nodePaymentRequestCreatePaymentLinkQueue;
    @Value("${spring.rabbitmq.queues.node_task_request-process-task-queue}")
    private String nodeTaskRequestProcessTaskQueue;
    @Value("${spring.rabbitmq.queues.node_task_request-cancel-task-queue}")
    private String nodeTaskRequestCancelTaskQueue;
    @Value("${spring.rabbitmq.queues.node_task_payment-status-queue}")
    private String nodeTaskPaymentStatusQueue;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendToDispatcherResponseSendMessage(SendMessage sendMessage) {
        log.debug("Sending to dispatcher Response SendMessage");
        rabbitTemplate.convertAndSend(nodeDispatcherResponseSendMessageQueue, sendMessage);
    }

    @Override
    public void sendToDispatcherResponseEditMessageText(EditMessageText editMessageText) {
        log.debug("Sending to dispatcher Response EditMessageText");
        rabbitTemplate.convertAndSend(nodeDispatcherResponseEditMessageTextQueue, editMessageText);
    }

    @Override
    public void sendToPaymentRequestCreatePaymentLink(CreatePaymentLinkRequest createPaymentLinkRequest) {
        log.debug("Sending to payment Request CreatePaymentLinkRequest");
        rabbitTemplate.convertAndSend(nodePaymentRequestCreatePaymentLinkQueue, createPaymentLinkRequest);
    }

    @Override
    public void sendToTaskRequestProcessTask(ProcessTaskRequest processTaskRequest) {
        log.debug("Sending to task Request ProcessTaskRequest");
        rabbitTemplate.convertAndSend(nodeTaskRequestProcessTaskQueue, processTaskRequest);
    }

    @Override
    public void sendToTaskRequestCancelTask(Integer currentTaskId) {
        log.debug("Sending to task Request CanceledTask");
        rabbitTemplate.convertAndSend(nodeTaskRequestCancelTaskQueue, currentTaskId);
    }

    @Override
    public void sendToTaskPaymentStatus(PaymentStatus paymentStatus) {
        log.debug("Sending to task PaymentStatus");
        rabbitTemplate.convertAndSend(nodeTaskPaymentStatusQueue, paymentStatus);
    }
}
