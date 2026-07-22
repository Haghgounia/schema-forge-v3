# SchemaForge v3 - Class Documentation

Version: schema-forge-v3-2026-07-22-1812

This document is generated from the project source tree and provides a
package/class inventory with a short architectural description.

Total Java source files analyzed: 345

## Package: `com.behsazan.schemaforge`

### SchemaForgeApplication

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/SchemaForgeApplication.java`

## Package: `com.behsazan.schemaforge.api`

### SpecificationArtifactController

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/api/SpecificationArtifactController.java`

### SystemController

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/api/SystemController.java`

## Package: `com.behsazan.schemaforge.api.common`

### ApiResponse

Type: **record**

Responsibility: REST/API layer component responsible for exposing
application capabilities.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/api/common/ApiResponse.java`

## Package: `com.behsazan.schemaforge.api.database`

### DatabaseInspectionController

Type: **class**

Responsibility: REST/API layer component responsible for exposing
application capabilities.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/api/database/DatabaseInspectionController.java`

### DatabaseInspectionSummary

Type: **record**

Responsibility: REST/API layer component responsible for exposing
application capabilities.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/api/database/DatabaseInspectionSummary.java`

## Package: `com.behsazan.schemaforge.api.error`

### ApiError

Type: **record**

Responsibility: REST/API layer component responsible for exposing
application capabilities.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/api/error/ApiError.java`

### GlobalExceptionHandler

Type: **class**

Responsibility: REST/API layer component responsible for exposing
application capabilities.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/api/error/GlobalExceptionHandler.java`

## Package: `com.behsazan.schemaforge.api.system`

### SystemStatusResponse

Type: **record**

Responsibility: REST/API layer component responsible for exposing
application capabilities.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/api/system/SystemStatusResponse.java`

## Package: `com.behsazan.schemaforge.application`

### ArtifactGenerationService

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/application/ArtifactGenerationService.java`

### DatabaseInspectionService

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/application/DatabaseInspectionService.java`

### DatabaseInspectionServiceImpl

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/application/DatabaseInspectionServiceImpl.java`

### GeneratedZip

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/application/GeneratedZip.java`

## Package: `com.behsazan.schemaforge.application.database`

### DatabaseMetadataReader

Type: **interface**

Responsibility: Application service/orchestration component coordinating
use cases.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/application/database/DatabaseMetadataReader.java`

### DatabaseMetadataReaderRegistry

Type: **class**

Responsibility: Application service/orchestration component coordinating
use cases.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/application/database/DatabaseMetadataReaderRegistry.java`

### DatabaseTableLookup

Type: **interface**

Responsibility: Application service/orchestration component coordinating
use cases.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/application/database/DatabaseTableLookup.java`

### GenerationDatabaseProductResolver

Type: **class**

Responsibility: Application service/orchestration component coordinating
use cases.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/application/database/GenerationDatabaseProductResolver.java`

## Package: `com.behsazan.schemaforge.comparison.column`

### AbstractColumnDifferenceRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/AbstractColumnDifferenceRule.java`

### ColumnComparisonRule

Type: **interface**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/ColumnComparisonRule.java`

### CommentComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/CommentComparisonRule.java`

### DataTypeNameComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/DataTypeNameComparisonRule.java`

### DefaultComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/DefaultComparisonRule.java`

### IdentityComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/IdentityComparisonRule.java`

### LengthComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/LengthComparisonRule.java`

### NullableComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/NullableComparisonRule.java`

### PrecisionComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/PrecisionComparisonRule.java`

### ScaleComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/column/ScaleComparisonRule.java`

## Package: `com.behsazan.schemaforge.comparison.context`

### ColumnLookup

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/context/ColumnLookup.java`

### ComparisonContext

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/context/ComparisonContext.java`

### ComparisonContextFactory

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/context/ComparisonContextFactory.java`

## Package: `com.behsazan.schemaforge.comparison.engine`

### SchemaComparisonEngine

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/engine/SchemaComparisonEngine.java`

## Package: `com.behsazan.schemaforge.comparison.model`

### ComparisonDifference

Type: **record**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/model/ComparisonDifference.java`

### ComparisonDifferenceBuilder

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/model/ComparisonDifferenceBuilder.java`

### ComparisonSummary

Type: **record**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/model/ComparisonSummary.java`

### DifferenceScope

Type: **enum**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/model/DifferenceScope.java`

### DifferenceSeverity

Type: **enum**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/model/DifferenceSeverity.java`

### DifferenceType

Type: **enum**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/model/DifferenceType.java`

### ResolutionStrategy

Type: **enum**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/model/ResolutionStrategy.java`

### TableComparisonReport

Type: **record**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/model/TableComparisonReport.java`

## Package: `com.behsazan.schemaforge.comparison.normalizer`

### CheckExpressionNormalizer

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/normalizer/CheckExpressionNormalizer.java`

### DefaultValueNormalizer

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/normalizer/DefaultValueNormalizer.java`

### IdentifierNormalizer

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/normalizer/IdentifierNormalizer.java`

### TextNormalizer

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/normalizer/TextNormalizer.java`

## Package: `com.behsazan.schemaforge.comparison.policy`

### AuditColumnPolicy

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/policy/AuditColumnPolicy.java`

## Package: `com.behsazan.schemaforge.comparison.rule`

### ColumnDefinitionComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/rule/ColumnDefinitionComparisonRule.java`

### ColumnExistenceComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/rule/ColumnExistenceComparisonRule.java`

### ComparisonRule

Type: **interface**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/rule/ComparisonRule.java`

## Package: `com.behsazan.schemaforge.comparison.rule.constraint`

### CheckConstraintComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/rule/constraint/CheckConstraintComparisonRule.java`

### ForeignKeyComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/rule/constraint/ForeignKeyComparisonRule.java`

### PrimaryKeyComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/rule/constraint/PrimaryKeyComparisonRule.java`

### UniqueKeyComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/rule/constraint/UniqueKeyComparisonRule.java`

## Package: `com.behsazan.schemaforge.comparison.rule.index`

### IndexComparisonRule

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/rule/index/IndexComparisonRule.java`

## Package: `com.behsazan.schemaforge.comparison.signature`

### CheckConstraintSignatureFactory

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/signature/CheckConstraintSignatureFactory.java`

### ConstraintSignatureSupport

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/signature/ConstraintSignatureSupport.java`

### ForeignKeySignatureFactory

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/signature/ForeignKeySignatureFactory.java`

### IndexSignatureFactory

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/signature/IndexSignatureFactory.java`

### PrimaryKeySignatureFactory

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/signature/PrimaryKeySignatureFactory.java`

### UniqueKeySignatureFactory

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/signature/UniqueKeySignatureFactory.java`

## Package: `com.behsazan.schemaforge.comparison.support`

### DataTypeFormatter

Type: **class**

Responsibility: Schema comparison component implementing comparison
logic, rules, or signatures.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/comparison/support/DataTypeFormatter.java`

## Package: `com.behsazan.schemaforge.configuration`

### DdlEngineConfiguration

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/DdlEngineConfiguration.java`

### SchemaForgeConfiguration

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/SchemaForgeConfiguration.java`

### SpellCheckConfiguration

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/SpellCheckConfiguration.java`

## Package: `com.behsazan.schemaforge.configuration.oracle`

### OracleConnectionProperties

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/oracle/OracleConnectionProperties.java`

### OracleDataSourceConfiguration

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/oracle/OracleDataSourceConfiguration.java`

## Package: `com.behsazan.schemaforge.configuration.postgresql`

### PostgreSqlConnectionProperties

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/postgresql/PostgreSqlConnectionProperties.java`

### PostgreSqlDataSourceConfiguration

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/postgresql/PostgreSqlDataSourceConfiguration.java`

## Package: `com.behsazan.schemaforge.configuration.properties`

### DdlGenerationProperties

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/properties/DdlGenerationProperties.java`

### SchemaForgeProperties

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/properties/SchemaForgeProperties.java`

## Package: `com.behsazan.schemaforge.configuration.web`

### WebConfiguration

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/configuration/web/WebConfiguration.java`

## Package: `com.behsazan.schemaforge.database.domain`

### ColumnDataTypeUsage

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/ColumnDataTypeUsage.java`

### ColumnState

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/ColumnState.java`

### ConstraintState

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/ConstraintState.java`

### DatabaseDictionary

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/DatabaseDictionary.java`

### DatabaseInspectionResult

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/DatabaseInspectionResult.java`

### IndexState

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/IndexState.java`

### RoutineState

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/RoutineState.java`

### SequenceState

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/SequenceState.java`

### SynonymState

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/SynonymState.java`

### TriggerState

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/TriggerState.java`

### ViewState

Type: **record**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/domain/ViewState.java`

## Package: `com.behsazan.schemaforge.database.oracle`

### JdbcOracleMetadataProvider

Type: **class**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/oracle/JdbcOracleMetadataProvider.java`

### JdbcOracleMetadataRepository

Type: **class**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/oracle/JdbcOracleMetadataRepository.java`

### OracleCanonicalSchemaMapper

Type: **class**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/oracle/OracleCanonicalSchemaMapper.java`

### OracleDictionaryCache

Type: **class**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/oracle/OracleDictionaryCache.java`

### OracleMetadataProvider

Type: **interface**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/oracle/OracleMetadataProvider.java`

### OracleTableMetadataLookup

Type: **class**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/oracle/OracleTableMetadataLookup.java`

### remains

Type: **interface**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/oracle/OracleMetadataRepository.java`

## Package: `com.behsazan.schemaforge.database.postgresql`

### JdbcPostgreSqlMetadataProvider

Type: **class**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/postgresql/JdbcPostgreSqlMetadataProvider.java`

### PostgreSqlMetadataProvider

Type: **interface**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/postgresql/PostgreSqlMetadataProvider.java`

## Package: `com.behsazan.schemaforge.database.service`

### DatabaseDictionaryCache

Type: **class**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/service/DatabaseDictionaryCache.java`

## Package: `com.behsazan.schemaforge.database.spi`

### DatabaseDictionaryProvider

Type: **interface**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/spi/DatabaseDictionaryProvider.java`

### DatabaseMetadataRepository

Type: **interface**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/spi/DatabaseMetadataRepository.java`

### for

Type: **interface**

Responsibility: Database metadata access component and DBMS-specific
adapter.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/database/spi/DatabaseMetadataProvider.java`

## Package: `com.behsazan.schemaforge.dialect`

### AbstractDatabaseDialect

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/AbstractDatabaseDialect.java`

### DataTypeRules

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DataTypeRules.java`

### DatabaseCapabilities

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DatabaseCapabilities.java`

### DatabaseCapability

Type: **enum**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DatabaseCapability.java`

### DatabaseDialect

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DatabaseDialect.java`

### DatabaseProduct

Type: **enum**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DatabaseProduct.java`

### DdlGenerationPolicy

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DdlGenerationPolicy.java`

### DdlSyntax

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DdlSyntax.java`

### DialectDefaults

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DialectDefaults.java`

### DialectRegistry

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/DialectRegistry.java`

### IdentifierPolicy

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/IdentifierPolicy.java`

### IdentifierRules

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/IdentifierRules.java`

### LogicalDataType

Type: **enum**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/LogicalDataType.java`

### NamingStrategy

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/NamingStrategy.java`

### ReservedWordProvider

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/ReservedWordProvider.java`

### SqlTypeMapper

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/SqlTypeMapper.java`

## Package: `com.behsazan.schemaforge.dialect.oracle`

### OracleDataTypeRules

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleDataTypeRules.java`

### OracleDdlGenerationPolicy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleDdlGenerationPolicy.java`

### OracleDdlSyntax

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleDdlSyntax.java`

### OracleDialect

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleDialect.java`

### OracleIdentifierPolicy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleIdentifierPolicy.java`

### OracleIdentifierRules

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleIdentifierRules.java`

### OracleNamingStrategy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleNamingStrategy.java`

### OracleReservedWordProvider

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleReservedWordProvider.java`

### OracleSqlTypeMapper

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/oracle/OracleSqlTypeMapper.java`

## Package: `com.behsazan.schemaforge.dialect.postgresql`

### PostgreSqlDataTypeRules

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlDataTypeRules.java`

### PostgreSqlDdlGenerationPolicy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlDdlGenerationPolicy.java`

### PostgreSqlDdlSyntax

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlDdlSyntax.java`

### PostgreSqlDialect

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlDialect.java`

### PostgreSqlIdentifierPolicy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlIdentifierPolicy.java`

### PostgreSqlIdentifierRules

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlIdentifierRules.java`

### PostgreSqlNamingStrategy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlNamingStrategy.java`

### PostgreSqlReservedWordProvider

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlReservedWordProvider.java`

### PostgreSqlSqlTypeMapper

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/postgresql/PostgreSqlSqlTypeMapper.java`

## Package: `com.behsazan.schemaforge.dialect.standard`

### StandardDataTypeRules

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardDataTypeRules.java`

### StandardDdlGenerationPolicy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardDdlGenerationPolicy.java`

### StandardDdlSyntax

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardDdlSyntax.java`

### StandardDialect

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardDialect.java`

### StandardIdentifierPolicy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardIdentifierPolicy.java`

### StandardIdentifierRules

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardIdentifierRules.java`

### StandardNamingStrategy

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardNamingStrategy.java`

### StandardReservedWordProvider

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardReservedWordProvider.java`

### StandardSqlTypeMapper

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/dialect/standard/StandardSqlTypeMapper.java`

## Package: `com.behsazan.schemaforge.discovery.core`

### DiscoveryContext

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/core/DiscoveryContext.java`

### DiscoveryEngine

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/core/DiscoveryEngine.java`

### DiscoveryRule

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/core/DiscoveryRule.java`

## Package: `com.behsazan.schemaforge.discovery.domain`

### DiscoveryCategory

Type: **enum**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/domain/DiscoveryCategory.java`

### DiscoveryIssue

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/domain/DiscoveryIssue.java`

### DiscoveryResult

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/domain/DiscoveryResult.java`

### DiscoverySeverity

Type: **enum**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/domain/DiscoverySeverity.java`

## Package: `com.behsazan.schemaforge.discovery.rules`

### ConsistencyRuleSupport

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/rules/ConsistencyRuleSupport.java`

### DataTypeConsistencyRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/rules/DataTypeConsistencyRule.java`

### DefaultConsistencyRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/rules/DefaultConsistencyRule.java`

### FieldUsageRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/rules/FieldUsageRule.java`

### LengthConsistencyRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/rules/LengthConsistencyRule.java`

### NullableConsistencyRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/rules/NullableConsistencyRule.java`

## Package: `com.behsazan.schemaforge.discovery.snapshot`

### ColumnUsage

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/snapshot/ColumnUsage.java`

### DiscoverySnapshot

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/snapshot/DiscoverySnapshot.java`

### DiscoverySnapshotBuilder

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/discovery/snapshot/DiscoverySnapshotBuilder.java`

## Package: `com.behsazan.schemaforge.domain.enums`

### DatabaseType

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/DatabaseType.java`

### GenerationMode

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/GenerationMode.java`

### IndexType

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/IndexType.java`

### ObjectType

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/ObjectType.java`

### ParameterMode

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/ParameterMode.java`

### ReferentialAction

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/ReferentialAction.java`

### RoutineType

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/RoutineType.java`

### SortDirection

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/SortDirection.java`

### ValidationSeverity

Type: **enum**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/enums/ValidationSeverity.java`

## Package: `com.behsazan.schemaforge.domain.model`

### CheckConstraint

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/CheckConstraint.java`

### Column

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Column.java`

### DatabaseSchema

Type: **class**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/DatabaseSchema.java`

### ForeignKey

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/ForeignKey.java`

### Grant

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Grant.java`

### Index

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Index.java`

### IndexColumn

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/IndexColumn.java`

### PrimaryKey

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/PrimaryKey.java`

### Project

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Project.java`

### Routine

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Routine.java`

### RoutineParameter

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/RoutineParameter.java`

### SchemaObject

Type: **interface**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/SchemaObject.java`

### Sequence

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Sequence.java`

### Specification

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Specification.java`

### Synonym

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Synonym.java`

### Table

Type: **class**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Table.java`

### Trigger

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/Trigger.java`

### UniqueKey

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/UniqueKey.java`

### View

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/model/View.java`

## Package: `com.behsazan.schemaforge.domain.valueobject`

### DataType

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/valueobject/DataType.java`

### DefaultValue

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/valueobject/DefaultValue.java`

### Description

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/valueobject/Description.java`

### Identifier

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/valueobject/Identifier.java`

### QualifiedName

Type: **record**

Responsibility: Domain model component representing core business
concepts and rules.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/domain/valueobject/QualifiedName.java`

## Package: `com.behsazan.schemaforge.generation.artifact`

### ArtifactBundle

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/artifact/ArtifactBundle.java`

## Package: `com.behsazan.schemaforge.generation.core`

### DdlGenerationEngine

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/core/DdlGenerationEngine.java`

### DdlGenerationRequest

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/core/DdlGenerationRequest.java`

### DdlGenerationResult

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/core/DdlGenerationResult.java`

### DialectRegistry

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/core/DialectRegistry.java`

### GenerationService

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/core/GenerationService.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.comment`

### CommentGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/comment/CommentGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.constraint`

### CheckConstraintGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/constraint/CheckConstraintGenerator.java`

### ConstraintSqlSupport

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/constraint/ConstraintSqlSupport.java`

### ForeignKeyGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/constraint/ForeignKeyGenerator.java`

### PrimaryKeyGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/constraint/PrimaryKeyGenerator.java`

### UniqueKeyGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/constraint/UniqueKeyGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.grant`

### GrantGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/grant/GrantGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.index`

### IndexGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/index/IndexGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.schema`

### SchemaScriptGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/schema/SchemaScriptGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.script`

### TableScriptGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/script/TableScriptGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.sequence`

### SequenceGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/sequence/SequenceGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.storage`

### PhysicalOptionsRenderer

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/storage/PhysicalOptionsRenderer.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.table`

### ColumnDefinitionGenerator

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/table/ColumnDefinitionGenerator.java`

### ColumnDefinitionGeneratorRegistry

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/table/ColumnDefinitionGeneratorRegistry.java`

### DataTypeSqlRenderer

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/table/DataTypeSqlRenderer.java`

### IdentifierSqlRenderer

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/table/IdentifierSqlRenderer.java`

### TableDdlGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/table/TableDdlGenerator.java`

### TableGenerationException

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/table/TableGenerationException.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.table.oracle`

### OracleColumnDefinitionGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/table/oracle/OracleColumnDefinitionGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.generator.table.postgresql`

### PostgreSqlColumnDefinitionGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/generator/table/postgresql/PostgreSqlColumnDefinitionGenerator.java`

## Package: `com.behsazan.schemaforge.generation.ddl.model`

### DdlGenerationMessage

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlGenerationMessage.java`

### DdlGenerationResult

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlGenerationResult.java`

### DdlGenerationSeverity

Type: **enum**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlGenerationSeverity.java`

### DdlObjectReference

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlObjectReference.java`

### DdlPhase

Type: **enum**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlPhase.java`

### DdlScript

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlScript.java`

### DdlSection

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlSection.java`

### DdlStatement

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlStatement.java`

### DdlStatementType

Type: **enum**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/DdlStatementType.java`

### RenderContext

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/RenderContext.java`

### ScriptOptions

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/ScriptOptions.java`

### SqlBuilder

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/SqlBuilder.java`

### SqlFragment

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/SqlFragment.java`

### StatementOrder

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/model/StatementOrder.java`

## Package: `com.behsazan.schemaforge.generation.ddl.renderer`

### AbstractDdlRenderer

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/AbstractDdlRenderer.java`

### DdlRenderException

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/DdlRenderException.java`

### DdlRenderer

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/DdlRenderer.java`

### RenderedDdl

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/RenderedDdl.java`

### RendererRegistry

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/RendererRegistry.java`

### SqlWriter

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/SqlWriter.java`

## Package: `com.behsazan.schemaforge.generation.ddl.renderer.oracle`

### OracleDdlRenderer

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/oracle/OracleDdlRenderer.java`

## Package: `com.behsazan.schemaforge.generation.ddl.renderer.postgresql`

### PostgreSqlDdlRenderer

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/postgresql/PostgreSqlDdlRenderer.java`

## Package: `com.behsazan.schemaforge.generation.ddl.renderer.standard`

### StandardDdlRenderer

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/ddl/renderer/standard/StandardDdlRenderer.java`

## Package: `com.behsazan.schemaforge.generation.enrichment`

### AuditColumnSchemaEnricher

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/enrichment/AuditColumnSchemaEnricher.java`

### SchemaEnricher

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/enrichment/SchemaEnricher.java`

## Package: `com.behsazan.schemaforge.generation.model`

### SqlDocument

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/model/SqlDocument.java`

### SqlSection

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/model/SqlSection.java`

### SqlStatement

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/model/SqlStatement.java`

## Package: `com.behsazan.schemaforge.generation.oracle`

### OracleCanonicalTypeMapper

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleCanonicalTypeMapper.java`

### OracleColumnMetadataInspector

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleColumnMetadataInspector.java`

### OracleConstraintGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleConstraintGenerator.java`

### OracleDdlGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleDdlGenerator.java`

### OracleHintBuilder

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleHintBuilder.java`

### OracleIndexGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleIndexGenerator.java`

### OracleSequenceGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleSequenceGenerator.java`

### OracleSqlRenderer

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleSqlRenderer.java`

### OracleSynonymGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleSynonymGenerator.java`

### OracleTableGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleTableGenerator.java`

### OracleViewGenerator

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/oracle/OracleViewGenerator.java`

## Package: `com.behsazan.schemaforge.generation.plugin`

### DatabaseDdlPlugin

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/plugin/DatabaseDdlPlugin.java`

### DatabaseDdlPluginRegistry

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/plugin/DatabaseDdlPluginRegistry.java`

### DefaultDatabaseDdlPlugin

Type: **class**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/plugin/DefaultDatabaseDdlPlugin.java`

## Package: `com.behsazan.schemaforge.generation.spi`

### ArtifactType

Type: **enum**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/ArtifactType.java`

### DatabaseCapability

Type: **enum**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/DatabaseCapability.java`

### DatabaseDialect

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/DatabaseDialect.java`

### DatabaseType

Type: **enum**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/DatabaseType.java`

### DdlGenerator

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/DdlGenerator.java`

### GeneratedArtifact

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/GeneratedArtifact.java`

### GenerationContext

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/GenerationContext.java`

### GenerationMessage

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/GenerationMessage.java`

### GenerationOptions

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/GenerationOptions.java`

### GenerationResult

Type: **record**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/GenerationResult.java`

### GenerationSeverity

Type: **enum**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/GenerationSeverity.java`

### GenerationValidator

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/GenerationValidator.java`

### IdentifierRules

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/IdentifierRules.java`

### NamingStrategy

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/NamingStrategy.java`

### TypeMapper

Type: **interface**

Responsibility: DDL generation component responsible for database script
production.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/generation/spi/TypeMapper.java`

## Package: `com.behsazan.schemaforge.packaging`

### ArtifactPackager

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/packaging/ArtifactPackager.java`

### ZipArtifactPackager

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/packaging/ZipArtifactPackager.java`

## Package: `com.behsazan.schemaforge.reporting`

### CanonicalSchemaCompareExcelWriter

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/reporting/CanonicalSchemaCompareExcelWriter.java`

### SchemaCompareExcelWriter

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/reporting/SchemaCompareExcelWriter.java`

### SchemaExcelWriter

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/reporting/SchemaExcelWriter.java`

### TableDefinitionAdapter

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/reporting/TableDefinitionAdapter.java`

## Package: `com.behsazan.schemaforge.shared.web`

### CorrelationIdFilter

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/shared/web/CorrelationIdFilter.java`

## Package: `com.behsazan.schemaforge.specification.adapter.docx`

### DocxSpecificationParser

Type: **class**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/adapter/docx/DocxSpecificationParser.java`

## Package: `com.behsazan.schemaforge.specification.adapter.ea`

### EaXmiSpecificationParser

Type: **class**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/adapter/ea/EaXmiSpecificationParser.java`

## Package: `com.behsazan.schemaforge.specification.application`

### ParseSpecificationService

Type: **class**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/application/ParseSpecificationService.java`

## Package: `com.behsazan.schemaforge.specification.core`

### SpecificationParserRegistry

Type: **class**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/core/SpecificationParserRegistry.java`

## Package: `com.behsazan.schemaforge.specification.domain`

### ColumnDefinition

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/ColumnDefinition.java`

### DataTypeDefinition

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/DataTypeDefinition.java`

### ForeignKeyDefinition

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/ForeignKeyDefinition.java`

### IndexDefinition

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/IndexDefinition.java`

### PrimaryKeyDefinition

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/PrimaryKeyDefinition.java`

### SchemaDefinition

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/SchemaDefinition.java`

### SequenceDefinition

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/SequenceDefinition.java`

### TableDefinition

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/TableDefinition.java`

### for

Type: **interface**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/domain/DatabaseOptions.java`

## Package: `com.behsazan.schemaforge.specification.normalization.range`

### NumericRange

Type: **interface**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/normalization/range/NumericRange.java`

### NumericRangeParser

Type: **class**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/normalization/range/NumericRangeParser.java`

## Package: `com.behsazan.schemaforge.specification.recovery`

### DataTypeNormalizer

Type: **class**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/recovery/DataTypeNormalizer.java`

### IdentifierSanitizer

Type: **class**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/recovery/IdentifierSanitizer.java`

### RecoveryResult

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/recovery/RecoveryResult.java`

## Package: `com.behsazan.schemaforge.specification.spi`

### SpecificationParser

Type: **interface**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/spi/SpecificationParser.java`

### SpecificationSource

Type: **record**

Responsibility: Specification parsing and document model component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/specification/spi/SpecificationSource.java`

## Package: `com.behsazan.schemaforge.validation.core`

### CanonicalSchemaValidator

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/core/CanonicalSchemaValidator.java`

### ValidationContext

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/core/ValidationContext.java`

### ValidationDialectResolver

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/core/ValidationDialectResolver.java`

### ValidationEngine

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/core/ValidationEngine.java`

### ValidationRule

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/core/ValidationRule.java`

## Package: `com.behsazan.schemaforge.validation.domain`

### ValidationCode

Type: **enum**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/domain/ValidationCode.java`

### ValidationIssue

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/domain/ValidationIssue.java`

### ValidationResult

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/domain/ValidationResult.java`

### ValidationSeverity

Type: **enum**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/domain/ValidationSeverity.java`

## Package: `com.behsazan.schemaforge.validation.oracle`

### OracleDataTypeValidator

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/oracle/OracleDataTypeValidator.java`

### OracleIdentifierValidator

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/oracle/OracleIdentifierValidator.java`

### OracleLengthValidator

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/oracle/OracleLengthValidator.java`

### OracleReservedWordValidator

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/oracle/OracleReservedWordValidator.java`

## Package: `com.behsazan.schemaforge.validation.rules`

### ColumnNameSpellingRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/ColumnNameSpellingRule.java`

### ColumnValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/ColumnValidationRule.java`

### CrossReferenceValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/CrossReferenceValidationRule.java`

### DatabaseDataTypeValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/DatabaseDataTypeValidationRule.java`

### DatabaseIdentifierValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/DatabaseIdentifierValidationRule.java`

### ForeignKeyValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/ForeignKeyValidationRule.java`

### IndexValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/IndexValidationRule.java`

### OracleConstraintNameRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/OracleConstraintNameRule.java`

### OracleDataTypeValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/OracleDataTypeValidationRule.java`

### OracleIdentifierValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/OracleIdentifierValidationRule.java`

### OracleIndexNameRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/OracleIndexNameRule.java`

### OracleLengthValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/OracleLengthValidationRule.java`

### OracleReservedWordRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/OracleReservedWordRule.java`

### OracleSequenceNameRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/OracleSequenceNameRule.java`

### OracleTriggerNameRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/OracleTriggerNameRule.java`

### PrimaryKeyValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/PrimaryKeyValidationRule.java`

### RuleSupport

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/RuleSupport.java`

### SchemaValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/SchemaValidationRule.java`

### SequenceValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/SequenceValidationRule.java`

### TableValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/TableValidationRule.java`

### UniqueKeyValidationRule

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/rules/UniqueKeyValidationRule.java`

## Package: `com.behsazan.schemaforge.validation.spelling`

### LanguageToolSpellCheckService

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/spelling/LanguageToolSpellCheckService.java`

### NoOpSpellCheckService

Type: **class**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/spelling/NoOpSpellCheckService.java`

### SpellCheckService

Type: **interface**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/spelling/SpellCheckService.java`

### SpellingError

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/spelling/SpellingError.java`

### SpellingSuggestion

Type: **record**

Responsibility: Supporting infrastructure component.

Source:
`schema-forge-v3/src/main/java/com/behsazan/schemaforge/validation/spelling/SpellingSuggestion.java`
