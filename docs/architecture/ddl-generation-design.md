# SchemaForge v3 - DDL Generation Design

Version: schema-forge-v3-2026-07-22-1812

## Purpose

DDL Generation converts canonical schema definitions into executable
database scripts.

## Main Components

### DdlGenerationEngine

Responsibilities: - Coordinate generation process - Select database
dialect - Invoke generators

## Generated Artifacts

Examples:

-   CREATE TABLE
-   Primary Keys
-   Foreign Keys
-   Indexes
-   Comments
-   Sequences

## Flow

    Table Definition
            |
            v
    DDL Generation Engine
            |
            v
    Database Renderer
            |
            v
    SQL Script

## Design Principle

Generation is separated from parsing and comparison to support multiple
database platforms.
