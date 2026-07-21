package com.behsazan.schemaforge.dialect;

public interface DdlSyntax {

    String statementTerminator();

    String quoteIdentifier(String identifier);

    String currentTimestampExpression();

    boolean supportsCreateSequence();

    boolean supportsComments();
}
