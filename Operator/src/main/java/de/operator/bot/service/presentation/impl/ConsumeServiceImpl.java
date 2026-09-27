package de.operator.bot.service.presentation.impl;

import de.operator.bot.entity.Task;
import de.operator.bot.service.application.MainService;
import de.operator.bot.service.presentation.ConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumeServiceImpl implements ConsumerService {

    private final MainService mainService;

    @RabbitListener(queues = "${spring.rabbitmq.queues.operatordispatcher_operator_operator-update-queue}")
    @Override
    public void consumeFromOperatorDispatcherOperatorUpdate(Update update) {
        log.debug("Consuming from OperatorDispatcher Update");
        mainService.handleUpdate(update);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.payment_operator_hot-exception-queue}")
    @Override
    public void consumeFromPaymentHotException(String string) {
        log.debug("Consuming from Payment Hot-Exception");
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.task_operator_new-task-event-queue}")
    @Override
    public void consumeFromTaskNewTaskEvent(Task task) {
        log.debug("Consuming from Task new Task Event");
        mainService.handleNewTaskEvent(task);
    }
}
