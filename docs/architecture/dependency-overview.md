# SchemaForge v3 - Dependency Overview

Version: schema-forge-v3-2026-07-22-1812

## Main Runtime Flow

    Specification Document (DOCX)
              |
              v
    DocxSpecificationParser
              |
              v
    Canonical Domain Model
              |
              +----------------------+
              |                      |
              v                      v
     DDL Generation Engine     Comparison Engine
              |                      |
              v                      v
          SQL Script          Excel Report

## Artifact Generation Flow

    ArtifactGenerationService

            |
            +--> Generate DDL Script (always)
            |
            +--> Check Database Metadata
                        |
                        +--> Table Exists
                        |        |
                        |        +--> Generate Comparison Excel
                        |
                        +--> Table Not Exists
                                 |
                                 +--> Only SQL Output

## Dependency Direction

    API
     |
     v
    Application Services
     |
     +----------------+
     |                |
     v                v
    Domain       Infrastructure
     |
     +----------------+
                      |
                      v
              Database / Generation / Reporting

## Design Principles

-   Database independent domain model
-   Vendor specific metadata adapters
-   Rule based schema comparison
-   Separate DDL generation pipeline
-   Reusable reporting layer
