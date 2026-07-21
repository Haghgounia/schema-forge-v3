# Deprecated APIs

Deprecated classes remain available for compatibility and are planned for removal in Phase 3.6.

| Legacy API | Replacement | Since | Removal |
|---|---|---:|---:|
| `DdlGenerationEngine(DialectRegistry, RendererRegistry, ColumnDefinitionGeneratorRegistry)` | `DdlGenerationEngine(DatabaseDdlPluginRegistry)` | 3.4 | Phase 3.6 |
| `com.behsazan.schemaforge.dialect.DialectRegistry` | `DatabaseDdlPluginRegistry` | 3.4 | Phase 3.6 |
| `com.behsazan.schemaforge.generation.ddl.renderer.RendererRegistry` | `DatabaseDdlPluginRegistry` | 3.4 | Phase 3.6 |
| `ColumnDefinitionGeneratorRegistry` | `DatabaseDdlPluginRegistry` | 3.4 | Phase 3.6 |

Previously deprecated Oracle generator classes remain scheduled for removal in Phase 3.6.
