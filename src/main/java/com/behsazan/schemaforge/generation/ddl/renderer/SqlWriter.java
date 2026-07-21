package com.behsazan.schemaforge.generation.ddl.renderer;

import java.util.Objects;

public final class SqlWriter {
    private final String lineSeparator;
    private final StringBuilder content = new StringBuilder();

    public SqlWriter(String lineSeparator) {
        this.lineSeparator = Objects.requireNonNull(lineSeparator, "lineSeparator must not be null");
        if (lineSeparator.isEmpty()) {
            throw new IllegalArgumentException("lineSeparator must not be empty");
        }
    }

    public SqlWriter write(String value) {
        content.append(Objects.requireNonNull(value, "value must not be null"));
        return this;
    }

    public SqlWriter line(String value) {
        return write(value).newLine();
    }

    public SqlWriter newLine() {
        content.append(lineSeparator);
        return this;
    }

    public SqlWriter blankLine() {
        if (!content.isEmpty() && !endsWith(lineSeparator)) {
            newLine();
        }
        if (!content.isEmpty() && !endsWith(lineSeparator + lineSeparator)) {
            newLine();
        }
        return this;
    }

    public String content() {
        return content.toString();
    }

    private boolean endsWith(String suffix) {
        int start = content.length() - suffix.length();
        return start >= 0 && content.substring(start).equals(suffix);
    }
}
