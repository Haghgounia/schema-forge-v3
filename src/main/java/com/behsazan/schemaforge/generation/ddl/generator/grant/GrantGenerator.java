package com.behsazan.schemaforge.generation.ddl.generator.grant;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.domain.model.Grant;
import com.behsazan.schemaforge.generation.ddl.generator.table.IdentifierSqlRenderer;
import com.behsazan.schemaforge.generation.ddl.model.*;
import java.util.Objects;
import java.util.stream.Collectors;

public final class GrantGenerator {
    private final IdentifierSqlRenderer identifiers = new IdentifierSqlRenderer();

    public DdlStatement generate(Grant grant, DatabaseDialect dialect, int position) {
        Objects.requireNonNull(grant); Objects.requireNonNull(dialect);
        String privileges = grant.privileges().stream().collect(Collectors.joining(", "));
        String sql = "GRANT " + privileges + " ON " + identifiers.render(grant.target(), dialect)
                + " TO " + identifiers.render(grant.grantee(), dialect)
                + (grant.grantOption() ? " WITH GRANT OPTION" : "");
        return DdlStatement.of(DdlStatementType.GRANT,
                new DdlObjectReference(grant.target().schemaName().map(Object::toString).orElse(""),
                        grant.target().name().toString(), grant.objectType()),
                new StatementOrder(DdlPhase.GRANTS, position), SqlFragment.of(sql));
    }
}
