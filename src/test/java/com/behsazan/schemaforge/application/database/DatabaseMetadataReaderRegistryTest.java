package com.behsazan.schemaforge.application.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Table;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DatabaseMetadataReaderRegistryTest {
    @Test
    void resolvesReaderByDatabaseProduct() {
        DatabaseMetadataReader oracle = reader(DatabaseProduct.ORACLE);
        DatabaseMetadataReaderRegistry registry = new DatabaseMetadataReaderRegistry(List.of(oracle));
        assertThat(registry.require(DatabaseProduct.ORACLE)).isSameAs(oracle);
        assertThat(registry.find(DatabaseProduct.POSTGRESQL)).isEmpty();
    }

    @Test
    void rejectsDuplicateReadersForSameProduct() {
        assertThatThrownBy(() -> new DatabaseMetadataReaderRegistry(List.of(
                reader(DatabaseProduct.ORACLE), reader(DatabaseProduct.ORACLE))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate metadata reader");
    }

    private DatabaseMetadataReader reader(DatabaseProduct product) {
        return new DatabaseMetadataReader() {
            @Override public DatabaseProduct databaseProduct() { return product; }
            @Override public Optional<Table> readTable(String schemaName, String tableName) { return Optional.empty(); }
        };
    }
}
