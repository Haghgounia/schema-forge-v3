package com.behsazan.schemaforge.comparison.signature;

import com.behsazan.schemaforge.domain.model.UniqueKey;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public final class UniqueKeySignatureFactory {
    public String create(UniqueKey uniqueKey) {
        Objects.requireNonNull(uniqueKey, "uniqueKey must not be null");
        return "UK:" + ConstraintSignatureSupport.orderedColumns(uniqueKey.columns());
    }
}
