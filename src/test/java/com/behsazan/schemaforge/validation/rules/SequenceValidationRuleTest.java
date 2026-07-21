package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.*;
import com.behsazan.schemaforge.domain.valueobject.*;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class SequenceValidationRuleTest {
    @Test void shouldRejectStartOutsideSequenceRange() {
        Sequence sequence = new Sequence(QualifiedName.of("APP", "CUSTOMER_SEQ"), 100, 1, 1L, 10L, false, 20, null);
        ValidationContext context = context(DatabaseSchema.builder("APP").addSequence(sequence).build());
        new SequenceValidationRule().validate(context);
        assertTrue(context.result().issues().stream().anyMatch(i -> i.code() == ValidationCode.INVALID_SEQUENCE));
    }
    private ValidationContext context(DatabaseSchema schema) { ValidationContext c = new ValidationContext(); c.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema); return c; }
}
