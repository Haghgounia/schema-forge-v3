package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.DataTypeRules;
import java.util.Locale;
import java.util.Set;

public final class OracleDataTypeRules implements DataTypeRules {

    private static final Set<String> TYPES = Set.of(
            "BINARY_DOUBLE", "BINARY_FLOAT", "BLOB", "CHAR", "CLOB", "DATE", "FLOAT", "INTERVAL DAY TO SECOND",
            "INTERVAL YEAR TO MONTH", "LONG", "LONG RAW", "NCHAR", "NCLOB", "NUMBER", "NVARCHAR2", "RAW", "ROWID",
            "TIMESTAMP", "TIMESTAMP WITH LOCAL TIME ZONE", "TIMESTAMP WITH TIME ZONE", "UROWID", "VARCHAR2");

    @Override
    public boolean supports(String dataTypeName) {
        if (dataTypeName == null) {
            return false;
        }
        String normalized = normalize(dataTypeName);
        String baseType = normalized.replaceFirst("\\s*\\(.*$", "");
        return TYPES.contains(baseType);
    }

    @Override
    public Set<String> supportedDataTypes() {
        return TYPES;
    }

    @Override
    public String normalize(String dataTypeName) {
        return dataTypeName == null
                ? null
                : dataTypeName.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }
}
