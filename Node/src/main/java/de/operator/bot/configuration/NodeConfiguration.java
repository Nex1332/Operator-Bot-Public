package de.operator.bot.configuration;

import de.operator.bot.cache.AppUserCacheService;
import de.operator.bot.cache.OperatorCacheService;
import de.operator.bot.cache.TaskCacheService;
import de.operator.bot.repository.AppUserRepository;
import de.operator.bot.repository.OperatorRepository;
import de.operator.bot.repository.TaskRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class NodeConfiguration {
    @Bean
    public AppUserCacheService cacheService(AppUserRepository appUserRepository) {
        return new AppUserCacheService(appUserRepository);
    }

    @Bean
    public TaskCacheService taskCacheService(TaskRepository taskRepository) {
        return new TaskCacheService(taskRepository);
    }

    @Bean
    public AppUserCacheService appUserCacheService(AppUserRepository appUserRepository) {
        return new AppUserCacheService(appUserRepository);
    }

    @Bean
    public OperatorCacheService operatorCacheService(OperatorRepository operatorRepository) {
        return new OperatorCacheService(operatorRepository);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
