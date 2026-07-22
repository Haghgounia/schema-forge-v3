# SchemaForge v3 - Sequence Diagrams

Version: schema-forge-v3-2026-07-22-1812

## Scenario 1 - DOCX To SQL

    User
     |
     v
    ArtifactGenerationService
     |
     v
    DocxSpecificationParser
     |
     v
    Canonical Domain Model
     |
     v
    DdlGenerationEngine
     |
     v
    SQL Artifact

## Scenario 2 - Existing Database

    User
     |
     v
    ArtifactGenerationService
     |
     +--> DDL Generation
     |
     +--> DatabaseMetadataReader
              |
              v
         Table Exists
              |
              v
     Schema Comparison
              |
              v
     Excel Report

## Scenario 3 - New Table

    ArtifactGenerationService
     |
     v
    DatabaseMetadataReader
     |
     v
    Table Not Found
     |
     v
    SQL Script Only
