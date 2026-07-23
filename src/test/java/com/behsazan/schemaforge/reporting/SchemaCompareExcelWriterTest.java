package com.behsazan.schemaforge.reporting;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import com.behsazan.schemaforge.domain.valueobject.DefaultValue;
import com.behsazan.schemaforge.domain.valueobject.Description;
import com.behsazan.schemaforge.domain.valueobject.Identifier;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SchemaCompareExcelWriterTest {

    private final SchemaCompareExcelWriter writer =
            new SchemaCompareExcelWriter();


    @Test
    void createsComparisonWorkbookAndMarksDifferences() throws Exception {


        TableDefinition document =
                new TableDefinition(
                        "CIF",
                        "CUSTOMERS",
                        "Customers",

                        List.of(
                                new ColumnDefinition(
                                        "CUSTOMER_ID",
                                        new DataTypeDefinition(
                                                "NUMBER",
                                                null,
                                                20,
                                                null
                                        ),
                                        false,
                                        null,
                                        "Customer identifier",
                                        true,
                                        false,
                                        false
                                )
                        ),

                        null,
                        List.of(),
                        List.of(),
                        null,
                        Map.of()
                );


        Column databaseColumn =
                new Column(
                        Identifier.of("CUSTOMER_ID"),
                        DataType.numeric(
                                "NUMBER",
                                10,
                                null
                        ),
                        false,
                        new DefaultValue(null),
                        new Description(
                                "Customer identifier"
                        ),
                        false,
                        1
                );


        Table database =
                Table.builder(
                                "CIF",
                                "CUSTOMERS"
                        )
                        .addColumn(databaseColumn)
                        .build();



        byte[] bytes =
                writer.write(
                        document,
                        database,
                        Map.of(
                                "CUSTOMER_ID",
                                74
                        )
                );



        assertThat(bytes)
                .isNotEmpty();



        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook(
                                new ByteArrayInputStream(bytes)
                        )
        ) {


            org.apache.poi.ss.usermodel.Sheet sheet =
                    workbook.getSheet(
                            "CUSTOMERS"
                    );


            assertThat((Object) sheet)
                    .isNotNull();


            var row =
                    sheet.getRow(1);


            assertThat(row)
                    .isNotNull();



            /*
             * COLUMN_USAGE removed.
             *
             * New structure:
             *
             * 0  DOC_COLUMN_ID
             * 1  DOC_COLUMN_NAME
             * ...
             * 18 DIFF
             */


            assertThat(
                    row.getCell(0)
                            .getNumericCellValue()
            )
                    .isEqualTo(1);



            assertThat(
                    row.getCell(18)
                            .getStringCellValue()
            )
                    .contains("DATA_TYPE")
                    .contains("PRIMARY_KEY");
        }
    }
}