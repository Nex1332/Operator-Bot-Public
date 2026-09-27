package de.operator.bot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ProducerService {
    void sendToOperatorOperatorUpdate(Update update);
}
