# ADR-001: Canonical Model First

**Status:** Accepted

All input adapters, including DOCX and Enterprise Architect XMI, produce the same DBMS-neutral canonical model. Parsers must not produce SQL or reference concrete providers.
