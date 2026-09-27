package de.operator.bot.configuration;

import de.operator.bot.cache.OperatorCacheService;
import de.operator.bot.cache.TaskCacheService;
import de.operator.bot.repository.OperatorRepository;
import de.operator.bot.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OperatorConfiguration {
    @Bean
    public OperatorCacheService operatorCacheService(OperatorRepository operatorRepository) {
        return new OperatorCacheService(operatorRepository);
    }

    @Bean
    public TaskCacheService taskCacheService(TaskRepository taskRepository) {
        return new TaskCacheService(taskRepository);
    }
}
