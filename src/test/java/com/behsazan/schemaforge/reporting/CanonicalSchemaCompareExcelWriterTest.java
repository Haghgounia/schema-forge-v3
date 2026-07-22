package com.behsazan.schemaforge.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import java.io.ByteArrayInputStream;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

class CanonicalSchemaCompareExcelWriterTest {
    private final CanonicalSchemaCompareExcelWriter writer = new CanonicalSchemaCompareExcelWriter();

    @Test
    void createsSummaryDetailsAndAtomicDifferencesSheets() throws Exception {
        Table document = Table.builder("APP", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.simple("NUMBER")))
                .addColumn(Column.nullable("NAME", DataType.varchar("VARCHAR", 100)))
                .build();
        Table database = Table.builder("APP", "CUSTOMER")
                .addColumn(Column.required("ID", DataType.simple("VARCHAR2")))
                .build();

        byte[] bytes = writer.write(document, database, Map.of("ID", 25));

        assertThat(bytes).isNotEmpty();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertThat(workbook.getNumberOfSheets()).isEqualTo(3);
            assertThat(workbook.getSheet("TABLE_SUMMARY")).isNotNull();
            assertThat(workbook.getSheet("CUSTOMER")).isNotNull();
            assertThat(workbook.getSheet("DIFFERENCES")).isNotNull();
            assertThat(workbook.getSheet("TABLE_SUMMARY").getRow(5).getCell(1).getStringCellValue())
                    .isEqualTo("DIFFERENT");
            assertThat(workbook.getSheet("CUSTOMER").getRow(1).getCell(0).getNumericCellValue())
                    .isEqualTo(25);
            assertThat(workbook.getSheet("DIFFERENCES").getLastRowNum()).isGreaterThanOrEqualTo(2);
            assertThat(workbook.getSheet("DIFFERENCES").getRow(1).getCell(6).getStringCellValue())
                    .isIn("DATA_TYPE_CHANGED", "COLUMN_MISSING");

            assertThat(workbook.getSheet("DIFFERENCES").getRow(1).getCell(0).getStringCellValue())
                    .startsWith("CMP-");
            assertThat(workbook.getSheet("DIFFERENCES").getRow(1).getCell(10).getStringCellValue())
                    .isNotBlank();
            assertThat(workbook.getSheet("TABLE_SUMMARY").getRow(4).getCell(2).getHyperlink())
                    .isNotNull();
            assertThat(((XSSFSheet) workbook.getSheet("DIFFERENCES")).getCTWorksheet().isSetAutoFilter())
                    .isTrue();
        }
    }
}
