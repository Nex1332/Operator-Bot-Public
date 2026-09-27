package de.operator.bot.service;

import de.operator.bot.PaymentStatus;
import de.operator.bot.ProcessTaskRequest;
import de.operator.bot.TaskAnswerMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public interface ConsumerService {
        void consumeFromNodeRequestProcessTask(ProcessTaskRequest processTaskRequest);
        void consumeFromPaymentPaymentStatus(PaymentStatus paymentStatus);
        void consumeFromNodePaymentStatus(PaymentStatus paymentStatus);
        void consumeFromNodeRequestCancelTask(Integer taskId);
        void consumeFromOperatorResponseAnswerTaskAnswerMessage(TaskAnswerMessage taskAnswerMessage);
}
