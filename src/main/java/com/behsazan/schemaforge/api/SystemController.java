package com.behsazan.schemaforge.api;

import com.behsazan.schemaforge.api.common.ApiResponse;
import com.behsazan.schemaforge.api.system.SystemStatusResponse;
import com.behsazan.schemaforge.configuration.properties.SchemaForgeProperties;
import com.behsazan.schemaforge.dialect.DialectRegistry;
import com.behsazan.schemaforge.shared.web.CorrelationIdFilter;
import com.behsazan.schemaforge.specification.core.SpecificationParserRegistry;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {
    private final SchemaForgeProperties properties;
    private final DialectRegistry dialectRegistry;
    private final SpecificationParserRegistry parserRegistry;
    private final String version;

    public SystemController(
            SchemaForgeProperties properties,
            DialectRegistry dialectRegistry,
            SpecificationParserRegistry parserRegistry,
            @Value("${schemaforge.build.version:3.0.0-SNAPSHOT}") String version) {
        this.properties = properties;
        this.dialectRegistry = dialectRegistry;
        this.parserRegistry = parserRegistry;
        this.version = version;
    }

    @GetMapping("/status")
    public ApiResponse<SystemStatusResponse> status(HttpServletRequest request) {
        List<String> dialects = dialectRegistry.all().stream().map(d -> d.product().name()).sorted().toList();
        List<String> parsers = parserRegistry.all().stream().map(p -> p.getClass().getSimpleName()).sorted().toList();
        SystemStatusResponse response = new SystemStatusResponse(
                "schema-forge-v3", version, "UP", properties.generation().defaultDatabase(), dialects, parsers);
        String correlationId = String.valueOf(request.getAttribute(CorrelationIdFilter.REQUEST_ATTRIBUTE));
        return ApiResponse.success(response, correlationId);
    }
}
