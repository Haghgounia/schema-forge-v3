package com.behsazan.schemaforge.configuration;

import com.behsazan.schemaforge.generation.core.DialectRegistry;
import com.behsazan.schemaforge.generation.core.GenerationService;
import com.behsazan.schemaforge.generation.spi.DatabaseDialect;
import com.behsazan.schemaforge.specification.application.ParseSpecificationService;
import com.behsazan.schemaforge.specification.core.SpecificationParserRegistry;
import com.behsazan.schemaforge.specification.spi.SpecificationParser;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemaForgeConfiguration {
    @Bean
    DialectRegistry legacyDialectRegistry(List<DatabaseDialect> dialects) {
        return new DialectRegistry(dialects);
    }

    @Bean
    GenerationService generationService(DialectRegistry registry) {
        return new GenerationService(registry);
    }

    @Bean
    SpecificationParserRegistry specificationParserRegistry(List<SpecificationParser> parsers) {
        return new SpecificationParserRegistry(parsers);
    }

    @Bean
    ParseSpecificationService parseSpecificationService(SpecificationParserRegistry registry) {
        return new ParseSpecificationService(registry);
    }
}
