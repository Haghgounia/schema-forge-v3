package com.behsazan.schemaforge.dialect;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public final class DatabaseCapabilities {

    private final Set<DatabaseCapability> values;

    private DatabaseCapabilities(Set<DatabaseCapability> values) {
        this.values = Set.copyOf(values);
    }

    public static DatabaseCapabilities of(DatabaseCapability... capabilities) {
        Objects.requireNonNull(capabilities, "capabilities must not be null");
        EnumSet<DatabaseCapability> values = EnumSet.noneOf(DatabaseCapability.class);
        for (DatabaseCapability capability : capabilities) {
            values.add(Objects.requireNonNull(capability, "capability must not be null"));
        }
        return new DatabaseCapabilities(values);
    }

    public static DatabaseCapabilities none() {
        return new DatabaseCapabilities(Set.of());
    }

    public boolean supports(DatabaseCapability capability) {
        return values.contains(Objects.requireNonNull(capability, "capability must not be null"));
    }

    public Set<DatabaseCapability> values() {
        return values;
    }
}
