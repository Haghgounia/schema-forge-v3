package com.behsazan.schemaforge.application.database;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** Indexes metadata readers by DBMS and prevents ambiguous duplicate adapters. */
@Component
public final class DatabaseMetadataReaderRegistry {
    private final Map<DatabaseProduct, DatabaseMetadataReader> readers;

    public DatabaseMetadataReaderRegistry(List<DatabaseMetadataReader> readers) {
        EnumMap<DatabaseProduct, DatabaseMetadataReader> indexed = new EnumMap<>(DatabaseProduct.class);
        for (DatabaseMetadataReader reader : readers == null ? List.<DatabaseMetadataReader>of() : readers) {
            DatabaseMetadataReader duplicate = indexed.putIfAbsent(reader.databaseProduct(), reader);
            if (duplicate != null) {
                throw new IllegalStateException("Duplicate metadata reader for " + reader.databaseProduct());
            }
        }
        this.readers = Map.copyOf(indexed);
    }

    public Optional<DatabaseMetadataReader> find(DatabaseProduct product) {
        return Optional.ofNullable(readers.get(product));
    }

    public DatabaseMetadataReader require(DatabaseProduct product) {
        return find(product).orElseThrow(() ->
                new IllegalStateException("No database metadata reader registered for " + product));
    }
}
