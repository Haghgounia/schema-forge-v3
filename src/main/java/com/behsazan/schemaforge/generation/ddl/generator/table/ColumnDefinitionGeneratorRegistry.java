package com.behsazan.schemaforge.generation.ddl.generator.table;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Selects self-describing vendor column generators without conditionals in the engine. */
public final class ColumnDefinitionGeneratorRegistry {
    private final Map<DatabaseProduct, ColumnDefinitionGenerator> generators;

    public ColumnDefinitionGeneratorRegistry(
            Collection<? extends ColumnDefinitionGenerator> generators) {
        Objects.requireNonNull(generators, "generators must not be null");
        EnumMap<DatabaseProduct, ColumnDefinitionGenerator> indexed =
                new EnumMap<>(DatabaseProduct.class);
        for (ColumnDefinitionGenerator generator : generators) {
            Objects.requireNonNull(generator, "generator must not be null");
            ColumnDefinitionGenerator previous = indexed.putIfAbsent(generator.product(), generator);
            if (previous != null) {
                throw new IllegalArgumentException(
                        "duplicate column definition generator for " + generator.product());
            }
        }
        this.generators = Map.copyOf(indexed);
    }

    public ColumnDefinitionGenerator require(DatabaseProduct product) {
        ColumnDefinitionGenerator generator = generators.get(
                Objects.requireNonNull(product, "product must not be null"));
        if (generator == null) {
            throw new IllegalArgumentException(
                    "column definition generator is not registered: " + product);
        }
        return generator;
    }

    public boolean supports(DatabaseProduct product) {
        return generators.containsKey(Objects.requireNonNull(product, "product must not be null"));
    }

    public Set<DatabaseProduct> products() {
        return generators.keySet();
    }
}
