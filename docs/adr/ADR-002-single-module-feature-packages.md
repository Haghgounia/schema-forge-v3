# ADR-002: Single Maven Module with Feature Packages

**Status:** Accepted

SchemaForge v3 uses one Maven project and one deployable Spring Boot application. Logical boundaries are enforced through package structure and ArchUnit rather than Maven modules.
