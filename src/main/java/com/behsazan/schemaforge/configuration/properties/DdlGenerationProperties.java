package com.behsazan.schemaforge.configuration.properties;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schemaforge.ddl")
public record DdlGenerationProperties(
        boolean enabled,
        Path inputDirectory,
        Path outputDirectory,
        Dialects dialects,
        Options options,
        Report report) {

    public DdlGenerationProperties {
        dialects = dialects == null ? new Dialects(true, true) : dialects;
        options = options == null ? new Options(true, false, true, true) : options;
        report = report == null ? new Report(true, true) : report;
    }

    public record Dialects(boolean oracle, boolean postgresql) {}

    public record Options(
            boolean recursive,
            boolean strict,
            boolean overwrite,
            boolean continueOnError) {}

    public record Report(boolean csv, boolean summary) {}
}
