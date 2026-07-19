package com.behsazan.schemaforge.generation.core;

import com.behsazan.schemaforge.generation.spi.DatabaseDialect;
import com.behsazan.schemaforge.generation.spi.GenerationContext;
import com.behsazan.schemaforge.generation.spi.GenerationMessage;
import com.behsazan.schemaforge.generation.spi.GenerationResult;
import java.util.ArrayList;
import java.util.List;

public final class GenerationService {
    private final DialectRegistry registry;

    public GenerationService(DialectRegistry registry) {
        this.registry = registry;
    }

    public GenerationResult generate(GenerationContext context) {
        DatabaseDialect dialect = registry.require(context.databaseType());
        List<GenerationMessage> validationMessages = dialect.validator().validate(context);
        boolean invalid = validationMessages.stream()
                .anyMatch(message -> message.severity().name().equals("ERROR"));
        if (invalid) {
            return new GenerationResult(List.of(), validationMessages);
        }
        GenerationResult generated = dialect.ddlGenerator().generate(context);
        List<GenerationMessage> allMessages = new ArrayList<>(validationMessages);
        allMessages.addAll(generated.messages());
        return new GenerationResult(generated.artifacts(), allMessages);
    }
}
