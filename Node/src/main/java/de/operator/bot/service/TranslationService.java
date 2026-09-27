package de.operator.bot.service;

import java.util.Locale;

public interface TranslationService {
    String translate(String key, Locale locale, Object... args);
}
