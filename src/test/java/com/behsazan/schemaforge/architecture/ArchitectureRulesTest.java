package com.behsazan.schemaforge.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

class ArchitectureRulesTest {
    @Test
    void canonicalModelMustNotDependOnSpringOrDatabaseProviders() {
        var classes = new ClassFileImporter().importPackages("com.behsazan.schemaforge");
        noClasses()
                .that().resideInAPackage("..specification.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "..generation.oracle..", "..generation.db2zos..")
                .check(classes);
    }

    @Test
    void generationSpiMustNotDependOnConcreteProviders() {
        var classes = new ClassFileImporter().importPackages("com.behsazan.schemaforge");
        noClasses()
                .that().resideInAPackage("..generation.spi..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..generation.oracle..", "..generation.db2zos..", "..generation.postgresql..", "..generation.mysql..", "..generation.sqlserver..")
                .check(classes);
    }
}
