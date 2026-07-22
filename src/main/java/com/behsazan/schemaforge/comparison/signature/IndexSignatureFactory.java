package com.behsazan.schemaforge.comparison.signature;

import com.behsazan.schemaforge.comparison.normalizer.IdentifierNormalizer;
import com.behsazan.schemaforge.domain.model.Index;
import com.behsazan.schemaforge.domain.model.IndexColumn;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class IndexSignatureFactory {
    public String create(Index index) {
        return index.type().name() + "|" + index.columns().stream()
                .map(this::columnSignature)
                .collect(Collectors.joining(","));
    }

    private String columnSignature(IndexColumn column) {
        return IdentifierNormalizer.normalize(column.column()) + ":" + column.direction().name();
    }
}
