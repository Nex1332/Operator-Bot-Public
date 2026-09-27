package de.operator.bot.service.impl;

import de.operator.bot.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@RequiredArgsConstructor
@Service
public class TranslationServiceImpl implements TranslationService {
    private final MessageSource messageSource;

    @Override
    public String translate(String key, Locale locale, Object... args) {
        return messageSource.getMessage(key, args,  locale);
    }
}
