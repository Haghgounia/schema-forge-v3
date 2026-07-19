package com.behsazan.schemaforge.validation.core;

import com.behsazan.schemaforge.generation.spi.GenerationContext;
import com.behsazan.schemaforge.generation.spi.GenerationMessage;
import com.behsazan.schemaforge.generation.spi.GenerationSeverity;
import com.behsazan.schemaforge.generation.spi.GenerationValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CanonicalSchemaValidator implements GenerationValidator {

    @Override
    public List<GenerationMessage> validate(GenerationContext context) {
        List<GenerationMessage> messages = new ArrayList<>();
        Set<String> tableNames = new HashSet<>();

        context.schema().tables().forEach(table -> {
            String tableName =
                    table.qualifiedName().name().value();

            String normalizedTableName =
                    table.qualifiedName().name().normalized();

            if (!tableNames.add(normalizedTableName)) {
                messages.add(
                        new GenerationMessage(
                                GenerationSeverity.ERROR,
                                "DUPLICATE_TABLE",
                                "Duplicate table name",
                                tableName
                        )
                );
            }

            Set<String> columnNames = new HashSet<>();

            table.columns().forEach(column -> {
                String columnName = column.name().value();
                String normalizedColumnName = column.name().normalized();

                if (!columnNames.add(normalizedColumnName)) {
                    messages.add(
                            new GenerationMessage(
                                    GenerationSeverity.ERROR,
                                    "DUPLICATE_COLUMN",
                                    "Duplicate column name",
                                    tableName + "." + columnName
                            )
                    );
                }
            });
        });

        return List.copyOf(messages);
    }
}