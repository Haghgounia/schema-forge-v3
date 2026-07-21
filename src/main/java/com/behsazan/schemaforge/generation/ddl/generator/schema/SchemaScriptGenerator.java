package com.behsazan.schemaforge.generation.ddl.generator.schema;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.generation.ddl.generator.grant.GrantGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.script.TableScriptGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.sequence.SequenceGenerator;
import com.behsazan.schemaforge.generation.ddl.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class SchemaScriptGenerator {
    private final TableScriptGenerator tableScriptGenerator;
    private final SequenceGenerator sequenceGenerator;
    private final GrantGenerator grantGenerator;

    public SchemaScriptGenerator(TableScriptGenerator tableScriptGenerator) {
        this(tableScriptGenerator, new SequenceGenerator(), new GrantGenerator());
    }

    public SchemaScriptGenerator(TableScriptGenerator tableScriptGenerator,
                                 SequenceGenerator sequenceGenerator,
                                 GrantGenerator grantGenerator) {
        this.tableScriptGenerator = Objects.requireNonNull(tableScriptGenerator);
        this.sequenceGenerator = Objects.requireNonNull(sequenceGenerator);
        this.grantGenerator = Objects.requireNonNull(grantGenerator);
    }

    public DdlScript generate(DatabaseSchema schema, DatabaseDialect dialect) {
        Objects.requireNonNull(schema); Objects.requireNonNull(dialect);
        List<DdlSection> sections = new ArrayList<>();
        if (!schema.sequences().isEmpty()) {
            List<DdlStatement> statements = new ArrayList<>();
            for (int i = 0; i < schema.sequences().size(); i++)
                statements.add(sequenceGenerator.generate(schema.sequences().get(i), dialect, i));
            sections.add(new DdlSection("Sequences", DdlPhase.SEQUENCES, statements));
        }
        for (var table : schema.tables()) sections.addAll(tableScriptGenerator.generate(table, dialect).sections());
        if (!schema.grants().isEmpty()) {
            List<DdlStatement> statements = new ArrayList<>();
            for (int i = 0; i < schema.grants().size(); i++)
                statements.add(grantGenerator.generate(schema.grants().get(i), dialect, i));
            sections.add(new DdlSection("Grants", DdlPhase.GRANTS, statements));
        }
        return new DdlScript(sections);
    }
}
