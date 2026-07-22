package com.behsazan.schemaforge.comparison.integration;

import com.behsazan.schemaforge.application.database.DatabaseMetadataReader;
import com.behsazan.schemaforge.comparison.engine.SchemaComparisonEngine;
import com.behsazan.schemaforge.comparison.model.TableComparisonReport;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.reporting.SchemaCompareExcelWriter;
import com.behsazan.schemaforge.reporting.TableDefinitionAdapter;
import com.behsazan.schemaforge.specification.adapter.docx.DocxSpecificationParser;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import com.behsazan.schemaforge.specification.spi.SpecificationSource;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(properties = {
        "schemaforge.oracle.enabled=true",
        "schemaforge.oracle.url=jdbc:oracle:thin:@//localhost:1521/FREEPDB1",
        "schemaforge.oracle.username=SYSTEM",
        "schemaforge.oracle.password=Oracle123"
})
@Tag("oracle-integration")
class OracleTableComparisonIntegrationTest {


    @Autowired
    private DocxSpecificationParser docxSpecificationParser;


    @Autowired
    private DatabaseMetadataReader databaseMetadataReader;


    @Autowired
    private SchemaComparisonEngine comparisonEngine;

    @Autowired
    private SchemaCompareExcelWriter excelWriter;

    @Test
    void compareCitiesAgainstOracle() throws Exception {



        SpecificationSource source =
                new SpecificationSource(
                        "MCB.BIM.TBL.CITIES.V1.2.docx",
                        new FileInputStream(
                                Path.of(
                                        "D:/SchemaDocuments/MCB.BIM.TBL.CITIES.V1.2.docx"
                                ).toFile()
                        )
                );


        DatabaseSchema documentSchema =
                docxSpecificationParser.parse(source);


        Table documentTable =
                documentSchema.tables()
                        .stream()
                        .filter(
                                table ->
                                        table.qualifiedName()
                                                .name()
                                                .value()
                                                .equalsIgnoreCase("CITIES")
                        )
                        .findFirst()
                        .orElseThrow();


        Table oracleTable =
                databaseMetadataReader.readTable(
                                "BIM",
                                "CITIES"
                        )
                        .orElseThrow();


        TableComparisonReport report =
                comparisonEngine.compare(
                        documentTable,
                        oracleTable
                );

        TableDefinition excelDocument =
                TableDefinitionAdapter.from(
                        documentTable
                );


        byte[] excel =
                excelWriter.write(
                        excelDocument,
                        oracleTable,
                        Map.of()
                );


        Path output =
                Path.of(
                        "target/comparison-report/BIM_CITIES_COMPARISON-"+ System.currentTimeMillis() +".xlsx"
                );


        Files.createDirectories(
                output.getParent()
        );


        Files.write(
                output,
                excel
        );


        System.out.println(
                "TABLE : "
                        + report.tableName()
        );

        System.out.println(
                "DIFFERENCES : "
                        + report.differences().size()
        );


        report.differences()
                .forEach(
                        difference -> {

                            System.out.println(
                                    difference.type()
                            );

                            System.out.println(
                                    difference.objectName()
                            );
                        }
                );


        assertNotNull(report);

    }
}