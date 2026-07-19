package com.behsazan.schemaforge.packaging;

import com.behsazan.schemaforge.generation.artifact.ArtifactBundle;
import com.behsazan.schemaforge.generation.spi.GeneratedArtifact;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipArtifactPackager implements ArtifactPackager {
    @Override
    public byte[] packageArtifacts(ArtifactBundle bundle) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(output)) {
            for (GeneratedArtifact artifact : bundle.artifacts()) {
                zip.putNextEntry(new ZipEntry(artifact.fileName()));
                zip.write(artifact.content());
                zip.closeEntry();
            }
            zip.finish();
            return output.toByteArray();
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to package generated artifacts", exception);
        }
    }
}
