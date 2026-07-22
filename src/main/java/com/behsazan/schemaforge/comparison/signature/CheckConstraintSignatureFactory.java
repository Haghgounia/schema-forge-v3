package com.behsazan.schemaforge.comparison.signature;

import com.behsazan.schemaforge.comparison.normalizer.CheckExpressionNormalizer;
import com.behsazan.schemaforge.domain.model.CheckConstraint;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public final class CheckConstraintSignatureFactory {
    private final CheckExpressionNormalizer normalizer;

    public CheckConstraintSignatureFactory() {
        this(new CheckExpressionNormalizer());
    }

    public CheckConstraintSignatureFactory(CheckExpressionNormalizer normalizer) {
        this.normalizer = Objects.requireNonNull(normalizer, "normalizer must not be null");
    }

    public String create(CheckConstraint constraint) {
        Objects.requireNonNull(constraint, "constraint must not be null");
        return "CHECK:" + normalizer.normalize(constraint.expression());
    }
}
