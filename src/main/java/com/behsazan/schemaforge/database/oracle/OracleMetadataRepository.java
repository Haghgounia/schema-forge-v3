package com.behsazan.schemaforge.database.oracle;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.domain.ColumnState;
import com.behsazan.schemaforge.database.domain.ConstraintState;
import com.behsazan.schemaforge.database.domain.IndexState;
import com.behsazan.schemaforge.database.domain.RoutineState;
import com.behsazan.schemaforge.database.domain.SequenceState;
import com.behsazan.schemaforge.database.domain.SynonymState;
import com.behsazan.schemaforge.database.domain.TriggerState;
import com.behsazan.schemaforge.database.domain.ViewState;
import com.behsazan.schemaforge.database.spi.DatabaseMetadataRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OracleMetadataRepository extends DatabaseMetadataRepository {
    boolean schemaExists(String schema);
    boolean tablespaceExists(String tablespace);
    boolean tableExists(String owner, String tableName);
    List<String> findTableNames(String owner);
    String findTableComment(String owner, String tableName);
    List<ColumnState> findColumns(String owner, String tableName);
    List<ConstraintState> findConstraints(String owner, String tableName);
    List<IndexState> findIndexes(String owner, String tableName);
    List<SequenceState> findSequences(String owner);
    List<ViewState> findViews(String owner);
    List<ViewState> findMaterializedViews(String owner);
    List<SynonymState> findSynonyms(String owner);
    List<TriggerState> findTriggers(String owner);
    List<RoutineState> findStandaloneRoutines(String owner);
    Set<String> loadReservedWords();
    Map<String, Integer> loadColumnUsageCounts();
    Map<String, List<ColumnDataTypeUsage>> loadColumnDataTypeUsages();
}
