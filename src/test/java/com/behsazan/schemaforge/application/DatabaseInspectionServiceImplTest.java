package com.behsazan.schemaforge.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.behsazan.schemaforge.database.spi.DatabaseMetadataRepository;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DatabaseInspectionServiceImplTest {
    @Test
    void delegatesInspectionToMatchingProvider() {
        DatabaseMetadataRepository repository = Mockito.mock(DatabaseMetadataRepository.class);
        when(repository.databaseType()).thenReturn(DatabaseType.ORACLE);
        when(repository.inspectSchema("BIM")).thenReturn(DatabaseSchema.builder("BIM").build());
        DatabaseInspectionService service = new DatabaseInspectionServiceImpl(List.of(repository));

        assertThat(service.inspect(DatabaseType.ORACLE, "BIM").schema().name().value()).isEqualTo("BIM");
    }

    @Test
    void rejectsMissingProvider() {
        DatabaseInspectionService service = new DatabaseInspectionServiceImpl(List.of());
        assertThatThrownBy(() -> service.inspect(DatabaseType.POSTGRESQL, "PUBLIC"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No metadata repository");
    }
}
