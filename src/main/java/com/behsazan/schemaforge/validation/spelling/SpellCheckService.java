package com.behsazan.schemaforge.validation.spelling;

import java.util.List;

/** Checks an identifier or text and returns spelling findings without changing the source value. */
@FunctionalInterface
public interface SpellCheckService {
    List<SpellingError> check(String text);
}
