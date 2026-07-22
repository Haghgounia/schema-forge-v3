# SchemaForge v3 - Package Map

Version: schema-forge-v3-2026-07-22-1812

## Architecture Layers

## API Layer

Package: `com.behsazan.schemaforge.api`

Responsibility: Provides external entry points and REST API exposure.

Main responsibilities: - Receive requests - Validate input - Invoke
application services

## Application Layer

Package: `com.behsazan.schemaforge.application`

Responsibility: Application orchestration and use-case execution.

Main components: - ArtifactGenerationService - Database inspection
services - Application workflows

## Domain Layer

Package: `com.behsazan.schemaforge.domain`

Responsibility: Contains canonical domain model.

Main concepts: - Table - Column - Index - Constraint - Schema objects

This layer is independent from database vendors.

## Specification Layer

Package: `com.behsazan.schemaforge.specification`

Responsibility: Reads and normalizes external database specification
documents.

Main components: - DOCX Parser - Specification models - Normalization
utilities

## Database Layer

Package: `com.behsazan.schemaforge.database`

Responsibility: Provides database metadata access.

Supported implementations: - Oracle - PostgreSQL

Main capabilities: - Read tables - Read columns - Read constraints -
Read indexes

## Comparison Layer

Package: `com.behsazan.schemaforge.comparison`

Responsibility: Compares expected schema against actual database
metadata.

Main concepts: - Comparison Engine - Comparison Rules - Signature
Factories - Difference Model

## Generation Layer

Package: `com.behsazan.schemaforge.generation`

Responsibility: Generates database DDL scripts.

Main outputs: - CREATE TABLE - Constraints - Indexes - Comments -
Sequences

## Reporting Layer

Package: `com.behsazan.schemaforge.reporting`

Responsibility: Produces comparison reports.

Main output: - Excel comparison workbook

## Packaging Layer

Package: `com.behsazan.schemaforge.packaging`

Responsibility: Packages generated artifacts.

Outputs: - SQL files - Excel files - ZIP bundles
