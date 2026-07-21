package com.behsazan.schemaforge.generation.ddl.model;

public record ScriptOptions(
        boolean includePreamble,
        boolean includePostamble,
        boolean includeSectionHeaders,
        boolean terminateStatements,
        String lineSeparator) {

    public ScriptOptions {
        lineSeparator = lineSeparator == null || lineSeparator.isEmpty()
                ? System.lineSeparator()
                : lineSeparator;
    }

    public static ScriptOptions defaults() {
        return new ScriptOptions(true, true, true, true, System.lineSeparator());
    }
}
