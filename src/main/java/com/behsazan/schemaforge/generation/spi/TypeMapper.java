package com.behsazan.schemaforge.generation.spi;

import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;

public interface TypeMapper {
    String map(DataTypeDefinition dataType);
}
