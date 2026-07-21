package com.behsazan.schemaforge.generation.ddl.generator.storage;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import java.util.Map;
import java.util.Objects;

public final class PhysicalOptionsRenderer {
    public String render(Map<String, String> options, DatabaseDialect dialect) {
        Objects.requireNonNull(dialect, "dialect must not be null");
        return dialect.ddlGenerationPolicy().renderPhysicalOptions(options);
    }
}
