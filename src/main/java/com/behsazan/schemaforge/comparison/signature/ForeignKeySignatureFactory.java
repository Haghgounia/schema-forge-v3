package com.behsazan.schemaforge.comparison.signature;

import com.behsazan.schemaforge.domain.model.ForeignKey;
import com.behsazan.schemaforge.domain.valueobject.QualifiedName;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public final class ForeignKeySignatureFactory {
    public String create(ForeignKey foreignKey) {
        Objects.requireNonNull(foreignKey, "foreignKey must not be null");
        return identity(foreignKey)
                + "->" + referencedTable(foreignKey.referencedTable())
                + "." + ConstraintSignatureSupport.orderedColumns(foreignKey.referencedColumns())
                + ":DELETE=" + foreignKey.onDelete().name()
                + ":UPDATE=" + foreignKey.onUpdate().name();
    }

    public String identity(ForeignKey foreignKey) {
        Objects.requireNonNull(foreignKey, "foreignKey must not be null");
        return "FK:" + ConstraintSignatureSupport.orderedColumns(foreignKey.columns());
    }

    private static String referencedTable(QualifiedName name) {
        String table = name.name().normalized();
        return name.schemaName().map(schema -> schema.normalized() + "." + table).orElse(table);
    }
}
