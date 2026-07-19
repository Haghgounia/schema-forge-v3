package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.generation.model.SqlDocument;
import com.behsazan.schemaforge.generation.model.SqlSection;
import com.behsazan.schemaforge.generation.spi.ArtifactType;
import com.behsazan.schemaforge.generation.spi.DdlGenerator;
import com.behsazan.schemaforge.generation.spi.GeneratedArtifact;
import com.behsazan.schemaforge.generation.spi.GenerationContext;
import com.behsazan.schemaforge.generation.spi.GenerationMessage;
import com.behsazan.schemaforge.generation.spi.GenerationResult;
import com.behsazan.schemaforge.generation.spi.GenerationSeverity;
import java.util.ArrayList;
import java.util.List;

public final class OracleDdlGenerator implements DdlGenerator {
    private final OracleSequenceGenerator sequenceGenerator;
    private final OracleTableGenerator tableGenerator;
    private final OracleConstraintGenerator constraintGenerator;
    private final OracleIndexGenerator indexGenerator;
    private final OracleSqlRenderer renderer;

    public OracleDdlGenerator() {
        this(new OracleSequenceGenerator(), new OracleTableGenerator(), new OracleConstraintGenerator(),
                new OracleIndexGenerator(), new OracleSqlRenderer());
    }

    public OracleDdlGenerator(
            OracleSequenceGenerator sequenceGenerator,
            OracleTableGenerator tableGenerator,
            OracleConstraintGenerator constraintGenerator,
            OracleIndexGenerator indexGenerator,
            OracleSqlRenderer renderer) {
        this.sequenceGenerator = sequenceGenerator;
        this.tableGenerator = tableGenerator;
        this.constraintGenerator = constraintGenerator;
        this.indexGenerator = indexGenerator;
        this.renderer = renderer;
    }

    @Override
    public GenerationResult generate(GenerationContext context) {
        SqlDocument tableDocument = tableGenerator.generate(context.schema(), context.options().includeComments());
        List<SqlSection> sections = new ArrayList<>();

        addWhenNotEmpty(sections, sequenceGenerator.generate(context.schema()));
        tableDocument.sections().stream()
                .filter(section -> section.order() < 200)
                .forEach(sections::add);
        sections.addAll(constraintGenerator.generate(context.schema()));
        addWhenNotEmpty(sections, indexGenerator.generate(context.schema()));
        tableDocument.sections().stream()
                .filter(section -> section.order() >= 200)
                .forEach(sections::add);

        String sql = renderer.render(new SqlDocument(sections));
        GeneratedArtifact artifact = GeneratedArtifact.text(
                "install.sql", ArtifactType.SQL, sql, 100, "application/sql; charset=UTF-8");
        GenerationMessage message = new GenerationMessage(
                GenerationSeverity.INFO,
                "ORACLE-DDL-001",
                "Oracle DDL generated successfully",
                context.schema().name().value());
        return new GenerationResult(List.of(artifact), List.of(message));
    }

    private void addWhenNotEmpty(List<SqlSection> sections, SqlSection section) {
        if (!section.statements().isEmpty()) {
            sections.add(section);
        }
    }
}
