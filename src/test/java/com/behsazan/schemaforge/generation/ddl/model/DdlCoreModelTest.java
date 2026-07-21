package com.behsazan.schemaforge.generation.ddl.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class DdlCoreModelTest {

    @Test
    void ordersSectionsAndStatementsDeterministically() {
        DdlObjectReference table = new DdlObjectReference("APP", "CUSTOMER", "TABLE");
        DdlStatement second = DdlStatement.of(
                DdlStatementType.CREATE_TABLE,
                table,
                new StatementOrder(DdlPhase.TABLES, 20),
                SqlFragment.of("CREATE TABLE APP.CUSTOMER"));
        DdlStatement first = DdlStatement.of(
                DdlStatementType.CREATE_TABLE,
                table,
                new StatementOrder(DdlPhase.TABLES, 10),
                SqlFragment.of("DROP TABLE APP.CUSTOMER"));

        DdlSection tableSection = new DdlSection("Tables", DdlPhase.TABLES, List.of(second, first));
        DdlSection sequenceSection = new DdlSection("Sequences", DdlPhase.SEQUENCES, List.of());
        DdlScript script = new DdlScript(List.of(tableSection, sequenceSection));

        assertThat(script.sections()).extracting(DdlSection::phase)
                .containsExactly(DdlPhase.SEQUENCES, DdlPhase.TABLES);
        assertThat(script.statements()).containsExactly(first, second);
    }

    @Test
    void rejectsStatementFromAnotherSectionPhase() {
        DdlStatement statement = DdlStatement.of(
                DdlStatementType.CREATE_INDEX,
                new DdlObjectReference("APP", "IX_CUSTOMER", "INDEX"),
                StatementOrder.first(DdlPhase.INDEXES),
                SqlFragment.of("CREATE INDEX APP.IX_CUSTOMER"));

        assertThatThrownBy(() -> new DdlSection("Tables", DdlPhase.TABLES, List.of(statement)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("section phase");
    }

    @Test
    void buildsNormalizedSqlFragment() {
        SqlFragment fragment = new SqlBuilder()
                .append("CREATE TABLE")
                .append("APP.CUSTOMER")
                .append("(")
                .commaSeparated(List.of("ID NUMBER(10)", "NAME VARCHAR2(100)"))
                .append(")")
                .build();

        assertThat(fragment.value())
                .isEqualTo("CREATE TABLE APP.CUSTOMER ( ID NUMBER(10), NAME VARCHAR2(100) )");
    }

    @Test
    void exposesQualifiedObjectName() {
        assertThat(new DdlObjectReference("APP", "CUSTOMER", "table").qualifiedName())
                .isEqualTo("APP.CUSTOMER");
        assertThat(new DdlObjectReference(null, "CUSTOMER", "table").qualifiedName())
                .isEqualTo("CUSTOMER");
    }

    @Test
    void detectsGenerationErrors() {
        DdlGenerationResult result = new DdlGenerationResult(
                new DdlScript(List.of()),
                List.of(new DdlGenerationMessage(
                        DdlGenerationSeverity.ERROR,
                        "DDL-001",
                        "Generation failed",
                        "APP.CUSTOMER")));

        assertThat(result.hasErrors()).isTrue();
    }
}
