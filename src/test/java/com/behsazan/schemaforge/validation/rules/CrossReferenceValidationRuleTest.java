package com.behsazan.schemaforge.validation.rules;

import com.behsazan.schemaforge.domain.model.*;
import com.behsazan.schemaforge.domain.valueobject.*;
import com.behsazan.schemaforge.validation.core.ValidationContext;
import com.behsazan.schemaforge.validation.domain.ValidationCode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class CrossReferenceValidationRuleTest {
    @Test void shouldRejectTriggerReferencingMissingTable() {
        Trigger trigger = new Trigger(QualifiedName.of("APP", "TRG_CUSTOMERS"), QualifiedName.of("APP", "CUSTOMERS"),
                "BEFORE", "INSERT", "BEGIN NULL; END;", null);
        ValidationContext context = context(DatabaseSchema.builder("APP").addTrigger(trigger).build());
        new CrossReferenceValidationRule().validate(context);
        assertTrue(context.result().issues().stream().anyMatch(i -> i.code() == ValidationCode.INVALID_REFERENCE));
    }
    private ValidationContext context(DatabaseSchema schema) { ValidationContext c = new ValidationContext(); c.put(SchemaValidationRule.ATTRIBUTE_SCHEMA, schema); return c; }
}
