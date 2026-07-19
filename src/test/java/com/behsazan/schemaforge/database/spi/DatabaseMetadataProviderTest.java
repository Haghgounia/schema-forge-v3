package com.behsazan.schemaforge.database.spi;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import org.junit.jupiter.api.Test;

class DatabaseMetadataProviderTest {

    @Test
    void exposesDbmsIdentityAndCanonicalSchema() {
        DatabaseMetadataProvider provider = new DatabaseMetadataProvider() {
            @Override
            public DatabaseType databaseType() {
                return DatabaseType.POSTGRESQL;
            }

            @Override
            public DatabaseSchema inspectSchema(String schemaName) {
                return DatabaseSchema.builder(schemaName).build();
            }
        };

        assertThat(provider.databaseType()).isEqualTo(DatabaseType.POSTGRESQL);
        assertThat(provider.inspectSchema("PUBLIC").name().value()).isEqualTo("PUBLIC");
    }
}
