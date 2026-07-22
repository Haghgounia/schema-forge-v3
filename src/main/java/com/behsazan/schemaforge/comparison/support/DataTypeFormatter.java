package com.behsazan.schemaforge.comparison.support;

import com.behsazan.schemaforge.domain.valueobject.DataType;

public final class DataTypeFormatter {
    private DataTypeFormatter() { }

    public static String format(DataType type) {
        String name = type.name().normalized();
        if (type.length() != null) return name + "(" + type.length() + ")";
        if (type.precision() != null) {
            return type.scale() == null
                    ? name + "(" + type.precision() + ")"
                    : name + "(" + type.precision() + "," + type.scale() + ")";
        }
        return name;
    }
}
