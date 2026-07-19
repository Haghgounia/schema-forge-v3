package com.behsazan.schemaforge.database.oracle;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "schemaforge.oracle", name = "enabled", havingValue = "true")
public class OracleDictionaryCache {

    private final OracleMetadataProvider repository;
    private Set<String> reservedWords = Set.of();
    private Map<String, Integer> columnUsageCounts = Map.of();
    private Map<String, List<ColumnDataTypeUsage>> columnDataTypeUsages = Map.of();

    public OracleDictionaryCache(OracleMetadataProvider repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @PostConstruct
    public void initialize() {
        Set<String> loadedReservedWords = repository.loadReservedWords();
        Map<String, Integer> loadedUsageCounts = repository.loadColumnUsageCounts();
        Map<String, List<ColumnDataTypeUsage>> loadedDataTypeUsages =
                repository.loadColumnDataTypeUsages();

        reservedWords = loadedReservedWords == null
                ? Set.of()
                : Collections.unmodifiableSet(loadedReservedWords);
        columnUsageCounts = loadedUsageCounts == null
                ? Map.of()
                : Collections.unmodifiableMap(loadedUsageCounts);
        columnDataTypeUsages = loadedDataTypeUsages == null
                ? Map.of()
                : Map.copyOf(loadedDataTypeUsages);
    }

    public boolean isReservedWord(String word) {
        return word != null
                && !word.isBlank()
                && reservedWords.contains(normalize(word));
    }

    public int getColumnUsageCount(String columnName) {
        if (columnName == null || columnName.isBlank()) {
            return 0;
        }
        return columnUsageCounts.getOrDefault(normalize(columnName), 0);
    }

    public List<ColumnDataTypeUsage> getColumnDataTypeUsages(String columnName) {
        if (columnName == null || columnName.isBlank()) {
            return List.of();
        }
        return columnDataTypeUsages.getOrDefault(normalize(columnName), List.of());
    }

    public void refresh() {
        initialize();
    }

    private String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
