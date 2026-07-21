package com.behsazan.schemaforge.dialect;

public interface NamingStrategy {

    String primaryKey(String tableName);

    String foreignKey(String tableName, String referencedTableName);

    String uniqueKey(String tableName, String suffix);

    String index(String tableName, String suffix);

    String sequence(String tableName);

    String trigger(String tableName, String suffix);
}
