package com.company.hrms.reporting.api.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Excel 匯出請求
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Schema(description = "Excel 匯出請求")
public class ExportExcelRequest {

    @Schema(description = "報表類型", example = "EMPLOYEE_ROSTER", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reportType;

    @Schema(description = "查詢條件")
    private java.util.Map<String, Object> filters;

    @Schema(description = "匯出欄位列表")
    private List<String> columns;

    @Schema(description = "檔案名稱", example = "員工花名冊")
    private String fileName;

    @Schema(description = "是否包含摘要")
    private Boolean includeSummary = true;
}
