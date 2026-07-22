package com.behsazan.schemaforge.comparison.normalizer;

import java.util.Locale;

/**
 * Produces a stable representation of a CHECK expression without changing
 * whitespace or character case inside quoted literals.
 */
public final class CheckExpressionNormalizer {
    public String normalize(String expression) {
        if (expression == null || expression.isBlank()) return "";

        String value = stripOuterParentheses(expression.trim());
        StringBuilder result = new StringBuilder(value.length());
        boolean singleQuote = false;
        boolean doubleQuote = false;
        boolean pendingSpace = false;

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);

            if (singleQuote) {
                result.append(current);
                if (current == '\'' && i + 1 < value.length() && value.charAt(i + 1) == '\'') {
                    result.append(value.charAt(++i));
                } else if (current == '\'') {
                    singleQuote = false;
                }
                continue;
            }

            if (doubleQuote) {
                result.append(current);
                if (current == '"' && i + 1 < value.length() && value.charAt(i + 1) == '"') {
                    result.append(value.charAt(++i));
                } else if (current == '"') {
                    doubleQuote = false;
                }
                continue;
            }

            if (current == '\'') {
                appendPendingSpace(result, pendingSpace);
                pendingSpace = false;
                singleQuote = true;
                result.append(current);
            } else if (current == '"') {
                appendPendingSpace(result, pendingSpace);
                pendingSpace = false;
                doubleQuote = true;
                result.append(current);
            } else if (Character.isWhitespace(current)) {
                pendingSpace = result.length() > 0;
            } else {
                if (pendingSpace && needsSeparator(result, current)) result.append(' ');
                pendingSpace = false;
                result.append(Character.toString(current).toUpperCase(Locale.ROOT));
            }
        }
        return stripOuterParentheses(result.toString().trim());
    }

    private static void appendPendingSpace(StringBuilder result, boolean pendingSpace) {
        if (pendingSpace && result.length() > 0 && needsSeparator(result, '\'')) result.append(' ');
    }

    private static boolean needsSeparator(StringBuilder result, char next) {
        if (result.length() == 0) return false;
        char previous = result.charAt(result.length() - 1);
        return isWordLike(previous) && isWordLike(next);
    }

    private static boolean isWordLike(char value) {
        return Character.isLetterOrDigit(value) || value == '_' || value == '$' || value == '#' || value == '\'' || value == '"';
    }

    private static String stripOuterParentheses(String input) {
        String value = input;
        while (hasSingleOuterPair(value)) value = value.substring(1, value.length() - 1).trim();
        return value;
    }

    private static boolean hasSingleOuterPair(String value) {
        if (value.length() < 2 || value.charAt(0) != '(' || value.charAt(value.length() - 1) != ')') return false;
        int depth = 0;
        boolean singleQuote = false;
        boolean doubleQuote = false;
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (singleQuote) {
                if (current == '\'' && i + 1 < value.length() && value.charAt(i + 1) == '\'') i++;
                else if (current == '\'') singleQuote = false;
                continue;
            }
            if (doubleQuote) {
                if (current == '"' && i + 1 < value.length() && value.charAt(i + 1) == '"') i++;
                else if (current == '"') doubleQuote = false;
                continue;
            }
            if (current == '\'') singleQuote = true;
            else if (current == '"') doubleQuote = true;
            else if (current == '(') depth++;
            else if (current == ')' && --depth == 0 && i < value.length() - 1) return false;
        }
        return depth == 0;
    }
}
