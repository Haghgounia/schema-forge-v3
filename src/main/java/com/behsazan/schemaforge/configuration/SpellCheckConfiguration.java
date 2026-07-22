package com.behsazan.schemaforge.configuration;

import com.behsazan.schemaforge.configuration.properties.SchemaForgeProperties;
import com.behsazan.schemaforge.validation.rules.ColumnNameSpellingRule;
import com.behsazan.schemaforge.validation.spelling.LanguageToolSpellCheckService;
import com.behsazan.schemaforge.validation.spelling.NoOpSpellCheckService;
import com.behsazan.schemaforge.validation.spelling.SpellCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Composition root for optional LanguageTool spelling validation. */
@Configuration
public class SpellCheckConfiguration {

    @Bean
    SpellCheckService spellCheckService(SchemaForgeProperties properties, ObjectMapper objectMapper) {
        if (!properties.spellCheck().enabled()) {
            return new NoOpSpellCheckService();
        }
        return new LanguageToolSpellCheckService(properties.spellCheck(), objectMapper);
    }

    @Bean
    ColumnNameSpellingRule columnNameSpellingRule(SpellCheckService spellCheckService) {
        return new ColumnNameSpellingRule(spellCheckService);
    }
}
