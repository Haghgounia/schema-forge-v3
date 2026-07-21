# Worst-case DOCX fixture generator

The test class `DocxWorstCaseFixtureGeneratorTest` scans a directory containing table-design DOCX files, collects representative column vocabulary, and writes one deliberately invalid document containing a broad range of data-quality defects.

Run on Windows:

```bat
mvn -Dtest=DocxWorstCaseFixtureGeneratorTest test ^
  -Dschemaforge.corpus.dir=D:\Specifications\Tables ^
  -Dschemaforge.worstcase.output=target\schemaforge-worst-case
```

Outputs:

- `WORST_CASE_TABLE_SPEC.docx`
- `WORST_CASE_MANIFEST.csv`

The fixture includes duplicate and normalized-duplicate field names, omitted names/types/descriptions, spelling mistakes, plural names, invalid/reserved/overlong identifiers, invisible characters, malformed or unsupported types, precision/scale problems, semantic type conflicts, invalid required markers and defaults, multiple PK markers, invalid references, duplicate UK/index positions, invalid ranges/checks, mixed-language identifiers, ambiguous abbreviations, inconsistent naming, and empty rows.

This test generates a negative test fixture. It does not claim to perform full Persian or English spell checking. Dictionary-based spelling validation should be implemented separately in the validation layer.
