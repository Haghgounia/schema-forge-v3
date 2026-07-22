# SchemaForge v3 - Configuration Guide

Version: schema-forge-v3-2026-07-22-1812

## Overview

This document describes runtime configuration areas of SchemaForge.

## Application Configuration

Main configuration areas:

-   Database connectivity
-   Database product selection
-   Artifact output
-   Logging
-   Runtime profiles

## Database Configuration

SchemaForge uses Database Metadata Reader adapters.

Configuration responsibilities:

-   Select target database product
-   Provide connection information
-   Enable metadata reading

## Supported Database Providers

Current architecture supports:

-   Oracle
-   PostgreSQL

## Output Configuration

Generated artifacts:

-   SQL scripts
-   Excel comparison reports
-   ZIP packages

## Runtime Profiles

Recommended profiles:

-   development
-   test
-   production

## Logging

Logging should capture:

-   Parsing activities
-   Metadata reading
-   DDL generation
-   Comparison execution
-   Artifact creation
