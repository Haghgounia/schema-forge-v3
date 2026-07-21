package com.behsazan.schemaforge.dialect;

import java.util.Set;

public interface DataTypeRules {

    boolean supports(String dataTypeName);

    Set<String> supportedDataTypes();

    String normalize(String dataTypeName);
}
