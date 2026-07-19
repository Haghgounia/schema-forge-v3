package com.behsazan.schemaforge.generation.spi;

import java.util.List;

public interface GenerationValidator {
    List<GenerationMessage> validate(GenerationContext context);
}
