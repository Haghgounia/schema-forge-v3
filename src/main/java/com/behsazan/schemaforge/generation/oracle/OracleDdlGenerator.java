package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.generation.model.SqlDocument;
import com.behsazan.schemaforge.generation.spi.ArtifactType;
import com.behsazan.schemaforge.generation.spi.DdlGenerator;
import com.behsazan.schemaforge.generation.spi.GeneratedArtifact;
import com.behsazan.schemaforge.generation.spi.GenerationContext;
import com.behsazan.schemaforge.generation.spi.GenerationMessage;
import com.behsazan.schemaforge.generation.spi.GenerationResult;
import com.behsazan.schemaforge.generation.spi.GenerationSeverity;
import java.util.List;

public final class OracleDdlGenerator implements DdlGenerator {
    private final OracleSequenceGenerator sequenceGenerator;
    private final OracleTableGenerator tableGenerator;
    private final OracleSqlRenderer renderer;

    public OracleDdlGenerator() {
        this(new OracleSequenceGenerator(), new OracleTableGenerator(), new OracleSqlRenderer());
    }

    public OracleDdlGenerator(OracleSequenceGenerator sequenceGenerator, OracleTableGenerator tableGenerator, OracleSqlRenderer renderer) {
        this.sequenceGenerator = sequenceGenerator;
        this.tableGenerator = tableGenerator;
        this.renderer = renderer;
    }

    @Override
    public GenerationResult generate(GenerationContext context) {
        SqlDocument tableDocument = tableGenerator.generate(context.schema(), context.options().includeComments());
        java.util.ArrayList<com.behsazan.schemaforge.generation.model.SqlSection> sections = new java.util.ArrayList<>();
        var sequenceSection = sequenceGenerator.generate(context.schema());
        if (!sequenceSection.statements().isEmpty()) sections.add(sequenceSection);
        sections.addAll(tableDocument.sections());
        SqlDocument document = new SqlDocument(sections);
        String sql = renderer.render(document);
        GeneratedArtifact artifact = GeneratedArtifact.text("install.sql", ArtifactType.SQL, sql, 100, "application/sql; charset=UTF-8");
        GenerationMessage message = new GenerationMessage(GenerationSeverity.INFO, "ORACLE-DDL-001",
                "Oracle sequence and table DDL generated successfully", context.schema().name().value());
        return new GenerationResult(List.of(artifact), List.of(message));
    }
}
