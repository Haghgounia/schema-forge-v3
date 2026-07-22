package com.behsazan.schemaforge.comparison.normalizer;

import java.util.Locale;

public final class DefaultValueNormalizer {
    private DefaultValueNormalizer() { }

    public static String normalize(String expression) {
        if (expression == null || expression.isBlank()) return "";
        String value = expression.trim();
        while (hasSingleOuterParentheses(value)) {
            value = value.substring(1, value.length() - 1).trim();
        }
        return collapseWhitespaceOutsideQuotes(value).toUpperCase(Locale.ROOT);
    }

    private static boolean hasSingleOuterParentheses(String value) {
        if (value.length() < 2 || value.charAt(0) != '(' || value.charAt(value.length() - 1) != ')') return false;
        boolean quoted = false;
        int depth = 0;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\'' && (i == 0 || value.charAt(i - 1) != '\\')) quoted = !quoted;
            if (quoted) continue;
            if (current == '(') depth++;
            if (current == ')') depth--;
            if (depth == 0 && i < value.length() - 1) return false;
        }
        return depth == 0;
    }

    private static String collapseWhitespaceOutsideQuotes(String value) {
        StringBuilder result = new StringBuilder(value.length());
        boolean quoted = false;
        boolean pendingSpace = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current == '\'' && (i == 0 || value.charAt(i - 1) != '\\')) {
                if (pendingSpace && !result.isEmpty()) result.append(' ');
                pendingSpace = false;
                quoted = !quoted;
                result.append(current);
            } else if (!quoted && Character.isWhitespace(current)) {
                pendingSpace = true;
            } else {
                if (pendingSpace && !result.isEmpty()) result.append(' ');
                pendingSpace = false;
                result.append(current);
            }
        }
        return result.toString().trim();
    }
}
