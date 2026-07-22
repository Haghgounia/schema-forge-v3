# SchemaForge v3 - Artifact Generation Flow

Version: schema-forge-v3-2026-07-22-1812

## Overview

ArtifactGenerationService is responsible for producing deployment and
analysis artifacts.

## Main Flow

    DOCX
     |
     v
    Specification Parser
     |
     v
    Canonical Domain Model
     |
     +----------------------+
     |                      |
     v                      v
    DDL Generation     Database Metadata Check
     |                      |
     v                      |
    SQL File               |
                            |
                     +------+------+
                     |             |
                  Exists       Not Exists
                     |
                     v
              Excel Comparison

## Output Rules

### Always Generated

SQL Script:

    TABLE-timestamp.sql

### Conditional

If database table exists:

    TABLE-timestamp.xlsx

## ZIP Packaging

Multiple artifacts are packaged into a ZIP bundle.
