package com.behsazan.schemaforge.database.spi;

/**
 * @deprecated Use {@link DatabaseMetadataProvider}. The repository name is kept
 * only as a source-compatible migration bridge.
 */
@Deprecated(forRemoval = true)
public interface DatabaseMetadataRepository extends DatabaseMetadataProvider {
}
