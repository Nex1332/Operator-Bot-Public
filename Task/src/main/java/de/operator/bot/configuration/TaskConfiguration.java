package de.operator.bot.configuration;

import de.operator.bot.cache.AppUserCacheService;
import de.operator.bot.cache.TaskCacheService;
import de.operator.bot.repository.AppUserRepository;
import de.operator.bot.repository.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TaskConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TaskCacheService taskCacheService(TaskRepository taskRepository) {
        return new TaskCacheService(taskRepository);
    }

    @Bean
    public AppUserCacheService appUserCacheService(AppUserRepository appUserRepository) {
        return new AppUserCacheService(appUserRepository);
    }
}
