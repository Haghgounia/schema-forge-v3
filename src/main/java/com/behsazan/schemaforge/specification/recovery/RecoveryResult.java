package com.behsazan.schemaforge.specification.recovery;

import java.util.List;
import java.util.Objects;

/** Result of a best-effort recovery operation. */
public record RecoveryResult(String value, List<String> warnings) {
    public RecoveryResult {
        Objects.requireNonNull(value, "value must not be null");
        warnings = warnings == null ? List.of() : List.copyOf(warnings);
    }

    public static RecoveryResult unchanged(String value) {
        return new RecoveryResult(value, List.of());
    }

    public static RecoveryResult recovered(String value, String warning) {
        return new RecoveryResult(value, List.of(warning));
    }
}
