package com.behsazan.schemaforge.comparison.signature;

import com.behsazan.schemaforge.domain.model.PrimaryKey;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public final class PrimaryKeySignatureFactory {
    public String create(PrimaryKey primaryKey) {
        Objects.requireNonNull(primaryKey, "primaryKey must not be null");
        return "PK:" + ConstraintSignatureSupport.orderedColumns(primaryKey.columns());
    }
}
