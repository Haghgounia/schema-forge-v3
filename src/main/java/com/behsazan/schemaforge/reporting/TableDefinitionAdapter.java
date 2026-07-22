package com.behsazan.schemaforge.reporting;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;

import java.util.List;
import java.util.Map;


public final class TableDefinitionAdapter {


    private TableDefinitionAdapter() {
    }


    public static TableDefinition from(Table table) {


        List<ColumnDefinition> columns =
                table.columns()
                        .stream()
                        .map(TableDefinitionAdapter::toColumnDefinition)
                        .toList();


        return new TableDefinition(

                table.qualifiedName()
                        .schemaName()
                        .map(identifier -> identifier.value())
                        .orElse(null),


                table.qualifiedName()
                        .name()
                        .value(),


                table.description()
                        .value(),


                columns,


                null,


                List.of(),


                List.of(),


                null,


                Map.of()
        );
    }



    private static ColumnDefinition toColumnDefinition(
            Column column) {


        return new ColumnDefinition(

                column.name()
                        .value(),


                toDataType(
                        column
                ),


                column.nullable(),


                column.defaultValue() == null
                        ? null
                        : column.defaultValue()
                          .expression(),


                column.description()
                        .value(),


                false,


                false,


                false
        );
    }



    private static DataTypeDefinition toDataType(
            Column column) {


        return new DataTypeDefinition(

                column.dataType()
                        .name()
                        .value(),


                column.dataType()
                        .length(),


                column.dataType()
                        .precision(),


                column.dataType()
                        .scale()
        );
    }
}