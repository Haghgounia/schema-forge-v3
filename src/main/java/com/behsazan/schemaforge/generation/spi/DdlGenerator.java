package com.behsazan.schemaforge.generation.spi;

public interface DdlGenerator {
    GenerationResult generate(GenerationContext context);
}
