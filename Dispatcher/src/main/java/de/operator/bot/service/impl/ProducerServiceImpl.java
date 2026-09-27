package de.operator.bot.service.impl;

import de.operator.bot.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Service
public class ProducerServiceImpl implements ProducerService {
    @Value("${spring.rabbitmq.queues.dispatcher_node_user-update-queue}")
    private String dispatcherNodeUserUpdateQueue;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendToNodeUserUpdate(Update update) {
        log.debug("Sending to Node UserUpdate");
        rabbitTemplate.convertAndSend(dispatcherNodeUserUpdateQueue, update);
    }
}

