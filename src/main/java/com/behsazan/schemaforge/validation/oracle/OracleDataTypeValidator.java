package com.behsazan.schemaforge.validation.oracle;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validates Oracle column data types.
 */
public final class OracleDataTypeValidator {

    private static final Set<String> SIMPLE_TYPES = Set.of(
            "DATE",
            "CLOB",
            "NCLOB",
            "BLOB",
            "XMLTYPE",
            "JSON",
            "FLOAT",
            "REAL",
            "BINARY_FLOAT",
            "BINARY_DOUBLE",
            "LONG",
            "LONG RAW",
            "ROWID",
            "UROWID"
    );

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile(
                    "^NUMBER\\s*(\\((\\d{1,2})(\\s*,\\s*(\\d{1,2}))?\\))?$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern VARCHAR2_PATTERN =
            Pattern.compile(
                    "^VARCHAR2\\((\\d{1,5})(\\s+(BYTE|CHAR))?\\)$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern NVARCHAR2_PATTERN =
            Pattern.compile(
                    "^NVARCHAR2\\((\\d{1,5})\\)$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern CHAR_PATTERN =
            Pattern.compile(
                    "^CHAR\\((\\d{1,5})(\\s+(BYTE|CHAR))?\\)$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern NCHAR_PATTERN =
            Pattern.compile(
                    "^NCHAR\\((\\d{1,5})\\)$",
                    Pattern.CASE_INSENSITIVE);

    private static final Pattern RAW_PATTERN =
            Pattern.compile(
                    "^RAW\\((\\d{1,5})\\)$",
                    Pattern.CASE_INSENSITIVE);

    public void requireValid(String dataType) {

        if (dataType == null || dataType.isBlank()) {
            throw new IllegalArgumentException("Data type is required.");
        }

        String value = normalize(dataType);

        if (SIMPLE_TYPES.contains(value)) {
            return;
        }

        if (NUMBER_PATTERN.matcher(value).matches()) {
            return;
        }

        if (VARCHAR2_PATTERN.matcher(value).matches()) {
            return;
        }

        if (NVARCHAR2_PATTERN.matcher(value).matches()) {
            return;
        }

        if (CHAR_PATTERN.matcher(value).matches()) {
            return;
        }

        if (NCHAR_PATTERN.matcher(value).matches()) {
            return;
        }

        if (RAW_PATTERN.matcher(value).matches()) {
            return;
        }

        throw new IllegalArgumentException(
                "Unsupported Oracle data type: " + dataType
        );
    }

    private String normalize(String text) {

        return text
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }
}