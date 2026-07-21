package com.behsazan.schemaforge.dialect.standard;

import com.behsazan.schemaforge.dialect.IdentifierPolicy;
import com.behsazan.schemaforge.dialect.NamingStrategy;

import java.util.Objects;

public final class StandardNamingStrategy implements NamingStrategy {

    private final IdentifierPolicy identifierPolicy;

    public StandardNamingStrategy(IdentifierPolicy identifierPolicy) {
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
        String value = identifierPolicy.normalize(String.join("_", parts)).replaceAll("[^A-Z0-9_]", "_");
        return value.length() <= identifierPolicy.maximumLength()
                ? value
                : value.substring(0, identifierPolicy.maximumLength());
    }
}
