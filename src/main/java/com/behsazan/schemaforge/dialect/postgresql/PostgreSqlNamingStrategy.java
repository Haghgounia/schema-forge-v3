package com.behsazan.schemaforge.dialect.postgresql;

import com.behsazan.schemaforge.dialect.IdentifierPolicy;
import com.behsazan.schemaforge.dialect.NamingStrategy;
import java.util.Objects;

public final class PostgreSqlNamingStrategy implements NamingStrategy {
    private final IdentifierPolicy policy;
    public PostgreSqlNamingStrategy(IdentifierPolicy policy) { this.policy = Objects.requireNonNull(policy); }
    @Override public String primaryKey(String tableName) { return compose("pk", tableName); }
    @Override public String foreignKey(String tableName, String referencedTableName) { return compose("fk", tableName, referencedTableName); }
    @Override public String uniqueKey(String tableName, String suffix) { return compose("uk", tableName, suffix); }
    @Override public String index(String tableName, String suffix) { return compose("ix", tableName, suffix); }
    @Override public String sequence(String tableName) { return compose("seq", tableName); }
    @Override public String trigger(String tableName, String suffix) { return compose("trg", tableName, suffix); }
    private String compose(String... parts) {
        String value = policy.normalize(String.join("_", parts)).replaceAll("[^a-z0-9_$]", "_");
        return value.length() <= policy.maximumLength() ? value : value.substring(0, policy.maximumLength());
    }
}
