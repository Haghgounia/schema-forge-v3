package com.behsazan.schemaforge.dialect;

public interface SqlTypeMapper {

    String map(LogicalDataType type);

    String map(LogicalDataType type, Integer length, Integer precision, Integer scale);
}
