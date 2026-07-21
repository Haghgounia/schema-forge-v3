SchemaForge Dialect Layer v1

Purpose:
- Introduce a database-neutral dialect abstraction before DDL generation.
- Keep Oracle-specific behavior inside the dialect.oracle package.
- Provide a neutral SQL Standard implementation for testing and future extension.

Main classes: 15
Tests: 2 test classes / 5 test methods

Installation:
1. Extract this archive at the project root.
2. Run: mvn clean test

No existing project class is overwritten in this package.
