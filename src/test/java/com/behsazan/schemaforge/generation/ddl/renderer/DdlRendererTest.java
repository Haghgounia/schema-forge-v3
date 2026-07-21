package com.behsazan.schemaforge.generation.ddl.renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.dialect.standard.StandardDialect;
import com.behsazan.schemaforge.generation.ddl.model.DdlObjectReference;
import com.behsazan.schemaforge.generation.ddl.model.DdlPhase;
import com.behsazan.schemaforge.generation.ddl.model.DdlScript;
import com.behsazan.schemaforge.generation.ddl.model.DdlSection;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatementType;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;
import com.behsazan.schemaforge.generation.ddl.model.ScriptOptions;
import com.behsazan.schemaforge.generation.ddl.model.SqlFragment;
import com.behsazan.schemaforge.generation.ddl.model.StatementOrder;
import com.behsazan.schemaforge.generation.ddl.renderer.oracle.OracleDdlRenderer;
import com.behsazan.schemaforge.generation.ddl.renderer.standard.StandardDdlRenderer;
import java.time.Clock;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DdlRendererTest {

    @Test
    void rendersOracleScriptWithHeadersAndTerminators() {
        DdlStatement statement = DdlStatement.of(
                DdlStatementType.CREATE_TABLE,
                new DdlObjectReference("APP", "CUSTOMER", "TABLE"),
                StatementOrder.first(DdlPhase.TABLES),
                SqlFragment.of("CREATE TABLE APP.CUSTOMER (ID NUMBER)"));
        DdlScript script = new DdlScript(List.of(new DdlSection(
                "Tables", DdlPhase.TABLES, List.of(statement))));
        RenderContext context = new RenderContext(
                new OracleDialect(), ScriptOptions.defaults(), Clock.systemUTC(), Map.of());

        RenderedDdl rendered = new OracleDdlRenderer().render(script, context);

        assertEquals(1, rendered.statementCount());
        assertTrue(rendered.content().contains("WHENEVER SQLERROR EXIT SQL.SQLCODE ROLLBACK"));
        assertTrue(rendered.content().contains("-- Tables"));
        assertTrue(rendered.content().contains("CREATE TABLE APP.CUSTOMER (ID NUMBER);"));
        assertTrue(rendered.content().contains("EXIT SUCCESS"));
    }

    @Test
    void rendersOracleTriggerWithSlashTerminator() {
        DdlStatement trigger = DdlStatement.of(
                DdlStatementType.CREATE_TRIGGER,
                new DdlObjectReference("APP", "TRG_CUSTOMER", "TRIGGER"),
                StatementOrder.first(DdlPhase.TRIGGERS),
                SqlFragment.of("CREATE OR REPLACE TRIGGER APP.TRG_CUSTOMER BEFORE INSERT ON APP.CUSTOMER BEGIN NULL; END;"));
        DdlScript script = new DdlScript(List.of(new DdlSection(
                "Triggers", DdlPhase.TRIGGERS, List.of(trigger))));
        RenderContext context = new RenderContext(
                new OracleDialect(), ScriptOptions.defaults(), Clock.systemUTC(), Map.of());

        String sql = new OracleDdlRenderer().render(script, context).content();

        assertTrue(sql.contains("END;" + System.lineSeparator() + "/"));
    }

    @Test
    void honorsDisabledFormattingOptions() {
        ScriptOptions options = new ScriptOptions(false, false, false, false, "\n");
        DdlStatement statement = DdlStatement.of(
                DdlStatementType.CUSTOM,
                new DdlObjectReference("", "CUSTOM", "CUSTOM"),
                StatementOrder.first(DdlPhase.TABLES),
                SqlFragment.of("CUSTOM SQL"));
        RenderContext context = new RenderContext(
                new StandardDialect(), options, Clock.systemUTC(), Map.of());

        String sql = new StandardDdlRenderer().render(
                new DdlScript(List.of(new DdlSection("Custom", DdlPhase.TABLES, List.of(statement)))),
                context).content();

        assertFalse(sql.contains("-- Custom"));
        assertFalse(sql.contains(";"));
        assertTrue(sql.contains("CUSTOM SQL"));
    }

    @Test
    void rejectsMismatchedDialect() {
        RenderContext context = new RenderContext(
                new StandardDialect(), ScriptOptions.defaults(), Clock.systemUTC(), Map.of());

        assertThrows(DdlRenderException.class,
                () -> new OracleDdlRenderer().render(new DdlScript(List.of()), context));
    }

    @Test
    void registrySelectsRendererAndRejectsDuplicates() {
        RendererRegistry registry = new RendererRegistry(List.of(
                new OracleDdlRenderer(), new StandardDdlRenderer()));

        assertTrue(registry.supports(DatabaseProduct.ORACLE));
        assertEquals(DatabaseProduct.ORACLE, registry.require(DatabaseProduct.ORACLE).product());
        assertThrows(IllegalArgumentException.class,
                () -> new RendererRegistry(List.of(new OracleDdlRenderer(), new OracleDdlRenderer())));
    }
}
