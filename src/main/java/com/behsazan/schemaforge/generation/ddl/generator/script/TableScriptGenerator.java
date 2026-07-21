package com.behsazan.schemaforge.generation.ddl.generator.script;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.generation.ddl.generator.constraint.CheckConstraintGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.constraint.ForeignKeyGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.constraint.PrimaryKeyGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.constraint.UniqueKeyGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.index.IndexGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.comment.CommentGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableDdlGenerator;
import com.behsazan.schemaforge.generation.ddl.model.DdlPhase;
import com.behsazan.schemaforge.generation.ddl.model.DdlScript;
import com.behsazan.schemaforge.generation.ddl.model.DdlSection;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class TableScriptGenerator {
    private final TableDdlGenerator tableGenerator;
    private final PrimaryKeyGenerator primaryKeyGenerator;
    private final UniqueKeyGenerator uniqueKeyGenerator;
    private final CheckConstraintGenerator checkConstraintGenerator;
    private final ForeignKeyGenerator foreignKeyGenerator;
    private final IndexGenerator indexGenerator;
    private final CommentGenerator commentGenerator;

    public TableScriptGenerator(TableDdlGenerator tableGenerator) {
        this(tableGenerator, new PrimaryKeyGenerator(), new UniqueKeyGenerator(), new CheckConstraintGenerator(), new ForeignKeyGenerator(), new IndexGenerator(), new CommentGenerator());
    }

    public TableScriptGenerator(
            TableDdlGenerator tableGenerator,
            PrimaryKeyGenerator primaryKeyGenerator,
            UniqueKeyGenerator uniqueKeyGenerator,
            CheckConstraintGenerator checkConstraintGenerator,
            ForeignKeyGenerator foreignKeyGenerator,
            IndexGenerator indexGenerator,
            CommentGenerator commentGenerator) {
        this.tableGenerator = Objects.requireNonNull(tableGenerator, "tableGenerator must not be null");
        this.primaryKeyGenerator = Objects.requireNonNull(primaryKeyGenerator, "primaryKeyGenerator must not be null");
        this.uniqueKeyGenerator = Objects.requireNonNull(uniqueKeyGenerator, "uniqueKeyGenerator must not be null");
        this.checkConstraintGenerator = Objects.requireNonNull(checkConstraintGenerator, "checkConstraintGenerator must not be null");
        this.foreignKeyGenerator = Objects.requireNonNull(foreignKeyGenerator, "foreignKeyGenerator must not be null");
        this.indexGenerator = Objects.requireNonNull(indexGenerator, "indexGenerator must not be null");
        this.commentGenerator = Objects.requireNonNull(commentGenerator, "commentGenerator must not be null");
    }

    public DdlScript generate(Table table, DatabaseDialect dialect) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        List<DdlSection> sections = new ArrayList<>();
        sections.add(new DdlSection("Create table", DdlPhase.TABLES,
                List.of(tableGenerator.generate(table, dialect, 0))));
        primaryKeyGenerator.generate(table, dialect, 0)
                .ifPresent(statement -> sections.add(section("Primary key", DdlPhase.PRIMARY_KEYS, List.of(statement))));
        addIfNotEmpty(sections, "Unique keys", DdlPhase.UNIQUE_KEYS,
                uniqueKeyGenerator.generate(table, dialect, 0));
        addIfNotEmpty(sections, "Check constraints", DdlPhase.CHECK_CONSTRAINTS,
                checkConstraintGenerator.generate(table, dialect, 0));
        addIfNotEmpty(sections, "Foreign keys", DdlPhase.FOREIGN_KEYS,
                foreignKeyGenerator.generate(table, dialect, 0));
        addIfNotEmpty(sections, "Indexes", DdlPhase.INDEXES,
                indexGenerator.generate(table, dialect, 0));
        addIfNotEmpty(sections, "Comments", DdlPhase.COMMENTS,
                commentGenerator.generate(table, dialect, 0));
        return new DdlScript(sections);
    }

    private static DdlSection section(String name, DdlPhase phase, List<DdlStatement> statements) {
        return new DdlSection(name, phase, statements);
    }

    private static void addIfNotEmpty(
            List<DdlSection> sections,
            String name,
            DdlPhase phase,
            List<DdlStatement> statements) {
        if (!statements.isEmpty()) {
            sections.add(section(name, phase, statements));
        }
    }
}
