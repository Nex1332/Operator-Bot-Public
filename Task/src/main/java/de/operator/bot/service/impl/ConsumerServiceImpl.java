package de.operator.bot.service.impl;

import de.operator.bot.PaymentStatus;
import de.operator.bot.ProcessTaskRequest;
import de.operator.bot.TaskAnswerMessage;
import de.operator.bot.service.ConsumerService;
import de.operator.bot.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.node_task_request-process-task-queue}")
    public void consumeFromNodeRequestProcessTask(ProcessTaskRequest processTaskRequest) {
        log.debug("Consuming from Node Request ProcessTaskRequest");
        mainService.handleProcessTaskRequest(processTaskRequest);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.payment_task_payment-status-queue}")
    public void consumeFromPaymentPaymentStatus(PaymentStatus paymentStatus) {
        log.debug("Consuming from Payment PaymentStatus");
        mainService.handlePaymentStatus(paymentStatus);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.node_task_payment-status-queue}")
    @Override
    public void consumeFromNodePaymentStatus(PaymentStatus paymentStatus) {
        log.debug("Consuming from Node PaymentStatus");
        mainService.handlePaymentStatus(paymentStatus);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.node_task_request-cancel-task-queue}")
    @Override
    public void consumeFromNodeRequestCancelTask(Integer taskId) {
        log.debug("Consuming from Node Request Cancel Task");
        mainService.handleRequestCancelTask(taskId);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.operator_task_response-answer-task-answer-message-queue}")
    @Override
    public void consumeFromOperatorResponseAnswerTaskAnswerMessage(TaskAnswerMessage taskAnswerMessage){
        log.debug("Consuming from Operator Response AnswerTaskMessage");
        mainService.handleProcessTaskAnswerMessage(taskAnswerMessage);
    }
}
