# ADR-003: Database Provider SPI

**Status:** Accepted

Each DBMS implements `DatabaseDialect`. Core orchestration depends only on SPI contracts and capabilities. Oracle, DB2 z/OS, PostgreSQL, SQL Server, and MySQL remain peer providers.
