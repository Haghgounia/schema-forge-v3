package com.behsazan.schemaforge.dialect;

import java.util.Set;

public interface ReservedWordProvider {

    boolean isReserved(String word);

    Set<String> words();
}
