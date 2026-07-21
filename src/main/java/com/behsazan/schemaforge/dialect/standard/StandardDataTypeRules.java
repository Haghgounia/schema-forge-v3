package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.DataTypeRules;
import java.util.Locale;
import java.util.Set;

public final class StandardDataTypeRules implements DataTypeRules {

    private static final Set<String> TYPES = Set.of(
            "BIGINT", "BOOLEAN", "CHAR", "DATE", "DECIMAL", "INTEGER", "NUMERIC", "SMALLINT", "TIME", "TIMESTAMP", "VARCHAR");

    @Override
    public boolean supports(String dataTypeName) {
        return dataTypeName != null && TYPES.contains(normalize(dataTypeName));
    }

    @Override
    public Set<String> supportedDataTypes() {
        return TYPES;
    }

    @Override
    public String normalize(String dataTypeName) {
        return dataTypeName == null ? null : dataTypeName.trim().toUpperCase(Locale.ROOT);
    }
}
