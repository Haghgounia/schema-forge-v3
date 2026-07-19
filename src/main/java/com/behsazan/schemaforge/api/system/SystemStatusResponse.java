package com.behsazan.schemaforge.api.system;

import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.util.List;

public record SystemStatusResponse(
        String application,
        String version,
        String status,
        DatabaseType defaultDatabase,
        List<String> registeredDialects,
        List<String> registeredParsers) {}
