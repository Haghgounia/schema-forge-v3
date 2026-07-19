package com.behsazan.schemaforge.api.database;

import com.behsazan.schemaforge.api.common.ApiResponse;
import com.behsazan.schemaforge.application.DatabaseInspectionService;
import com.behsazan.schemaforge.database.domain.DatabaseInspectionResult;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import com.behsazan.schemaforge.shared.web.CorrelationIdFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/database/inspection")
@Validated
public class DatabaseInspectionController {
    private final DatabaseInspectionService inspectionService;

    public DatabaseInspectionController(DatabaseInspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @GetMapping
    public ApiResponse<DatabaseInspectionResult> inspect(
            @RequestParam(defaultValue = "ORACLE") DatabaseType databaseType,
            @RequestParam @NotBlank String schemaName,
            HttpServletRequest request) {
        DatabaseInspectionResult result = inspectionService.inspect(databaseType, schemaName);
        return ApiResponse.success(result, correlationId(request));
    }

    @GetMapping("/summary")
    public ApiResponse<DatabaseInspectionSummary> inspectSummary(
            @RequestParam(defaultValue = "ORACLE") DatabaseType databaseType,
            @RequestParam @NotBlank String schemaName,
            HttpServletRequest request) {
        DatabaseInspectionResult result = inspectionService.inspect(databaseType, schemaName);
        return ApiResponse.success(DatabaseInspectionSummary.from(result), correlationId(request));
    }

    private String correlationId(HttpServletRequest request) {
        return String.valueOf(request.getAttribute(CorrelationIdFilter.REQUEST_ATTRIBUTE));
    }
}
