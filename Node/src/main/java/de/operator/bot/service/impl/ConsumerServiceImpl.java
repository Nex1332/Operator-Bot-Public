package de.operator.bot.service.impl;

import de.operator.bot.PaymentFeedback;
import de.operator.bot.TaskProcessingErrorEvent;
import de.operator.bot.service.*;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;
    private final UserInteractionService userInteractionService;

    @Timed(value = "time_of_process_of_a_request")
    @Counted(value = "user_updates")
    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.dispatcher_node_user-update-queue}")
    public void consumeFromDispatcherUserUpdate(Update update) {
        log.debug("Consuming from Dispatcher User Update");
        mainService.handleUpdate(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.payment_node_payment-feedback-queue}")
    public void consumeFromPaymentPaymentFeedback(PaymentFeedback paymentFeedback) {
        log.debug("Consuming from Payment PaymentFeedback");
        userInteractionService.handlePaymentFeedback(paymentFeedback);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.task_node_response-send-message-queue}")
    public void consumeFromTaskResponseSendMessage(SendMessage sendMessage){
        log.debug("Consuming from Task Response SendMessage");
        userInteractionService.handleTaskAnswerMessage(sendMessage);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.task_node_task-error-event-queue}")
    public void consumeFromTaskTaskErrorEvent(TaskProcessingErrorEvent taskProcessingErrorEvent){
        log.debug("Consuming from Task TaskProcessingErrorEvent");
        mainService.handleTaskErrorEvent(taskProcessingErrorEvent);
    }
}
