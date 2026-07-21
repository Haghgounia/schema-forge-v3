package com.behsazan.schemaforge.dialect;

import com.behsazan.schemaforge.domain.enums.IndexType;
import com.behsazan.schemaforge.domain.enums.ReferentialAction;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import java.util.Map;

/**
 * Database-specific DDL behavior used by the vendor-neutral generation engine.
 * Implementations belong to database plugins; core generators must not branch on DatabaseProduct.
 */
public interface DdlGenerationPolicy {

    String renderForeignKeyActions(ReferentialAction onDelete, ReferentialAction onUpdate);

    boolean qualifyIndexNameWithSchema();

    String indexTypePrefix(IndexType type);

    String noMinValueClause();

    String noMaxValueClause();

    String noCycleClause();

    String noCacheClause();

    String renderPhysicalOptions(Map<String, String> options);

    String renderDataType(DataType dataType, DatabaseDialect dialect);
}
