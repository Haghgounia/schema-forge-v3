package com.behsazan.schemaforge.application;

import com.behsazan.schemaforge.database.domain.DatabaseInspectionResult;
import com.behsazan.schemaforge.database.spi.DatabaseMetadataRepository;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInspectionServiceImpl implements DatabaseInspectionService {
    private final Map<DatabaseType, DatabaseMetadataRepository> repositories;

    public DatabaseInspectionServiceImpl(List<DatabaseMetadataRepository> repositories) {
        Objects.requireNonNull(repositories, "repositories must not be null");
        this.repositories = repositories.stream().collect(Collectors.toMap(
                DatabaseMetadataRepository::databaseType,
                Function.identity(),
                (first, second) -> { throw new IllegalStateException("Duplicate metadata repository for " + first.databaseType()); },
                () -> new EnumMap<>(DatabaseType.class)));
    }

    @Override
    public DatabaseInspectionResult inspect(DatabaseType databaseType, String schemaName) {
        DatabaseMetadataRepository repository = repositories.get(Objects.requireNonNull(databaseType));
        if (repository == null) throw new IllegalArgumentException("No metadata repository registered for " + databaseType);
        return new DatabaseInspectionResult(repository.inspectSchema(schemaName), Instant.now(), List.of());
    }
}
