package com.behsazan.schemaforge.dialect.oracle;

import com.behsazan.schemaforge.dialect.IdentifierPolicy;
import com.behsazan.schemaforge.dialect.NamingStrategy;

import java.util.Objects;

public final class OracleNamingStrategy implements NamingStrategy {

    private final IdentifierPolicy identifierPolicy;

    public OracleNamingStrategy(IdentifierPolicy identifierPolicy) {
        this.identifierPolicy = Objects.requireNonNull(identifierPolicy, "identifierPolicy must not be null");
    }

    @Override
    public String primaryKey(String tableName) {
        return compose("PK", tableName);
    }

    @Override
    public String foreignKey(String tableName, String referencedTableName) {
        return compose("FK", tableName, referencedTableName);
    }

    @Override
    public String uniqueKey(String tableName, String suffix) {
        return compose("UK", tableName, suffix);
    }

    @Override
    public String index(String tableName, String suffix) {
        return compose("IX", tableName, suffix);
    }

    @Override
    public String sequence(String tableName) {
        return compose("SEQ", tableName);
    }

    @Override
    public String trigger(String tableName, String suffix) {
        return compose("TRG", tableName, suffix);
    }

    private String compose(String... parts) {
        String value = String.join("_", parts);
        value = identifierPolicy.normalize(value).replaceAll("[^A-Z0-9_$#]", "_");
        int maximumLength = identifierPolicy.maximumLength();
        return value.length() <= maximumLength ? value : value.substring(0, maximumLength);
    }
}
