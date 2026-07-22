# SchemaForge v3 - Extension Guide

Version: schema-forge-v3-2026-07-22-1812

## Overview

SchemaForge is designed for extension through interfaces and adapters.

# Adding a New Database

Steps:

1.  Implement Database Metadata Reader
2.  Provide database specific mapping
3.  Register database provider
4.  Add integration tests

Example:

    DatabaseMetadataReader
              |
              +-- OracleReader
              |
              +-- PostgreSQLReader
              |
              +-- NewDatabaseReader

# Adding Comparison Rules

Steps:

1.  Implement ComparisonRule
2.  Define DifferenceType
3.  Add tests
4.  Register rule

# Adding DDL Support

Steps:

1.  Implement database dialect
2.  Add generators
3.  Add renderer
4.  Add generation tests

# Adding Reports

Reports should consume canonical comparison results and avoid
duplicating comparison logic.
