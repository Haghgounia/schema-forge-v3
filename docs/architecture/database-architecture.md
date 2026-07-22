# SchemaForge v3 - Database Architecture

Version: schema-forge-v3-2026-07-22-1812

## Overview

The database architecture layer provides database independent access to
metadata and database specific implementations.

## Main Components

### DatabaseMetadataReader

Responsibility: - Read live database metadata - Provide canonical table
information - Hide vendor specific catalog queries

Supported concepts: - Tables - Columns - Constraints - Indexes

## Oracle Implementation

Oracle adapter responsibilities: - Read Oracle dictionary metadata -
Convert Oracle structures into canonical domain objects - Support
comparison and discovery flows

## PostgreSQL Support

PostgreSQL implementation follows the same SPI contract and allows reuse
of: - Comparison Engine - Domain Model - Reporting Layer

## Architecture Principle

Database vendors are isolated behind adapters:

    Comparison Engine
            |
            v
    DatabaseMetadataReader SPI
            |
     +------+------+
     |             |
    Oracle      PostgreSQL
    Adapter     Adapter
