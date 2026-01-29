package com.company.hrms.recruitment.application.dto.report;

import java.time.LocalDate;

import org.springdoc.core.annotations.ParameterObject;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 報表匯出查詢條件
 */
@Data
@ParameterObject
@Schema(description = "報表匯出查詢條件")
public class ExportSearchDto {

    @Schema(description = "開始日期", example = "2025-12-01")
    private LocalDate dateFrom;

    @Schema(description = "結束日期", example = "2025-12-31")
    private LocalDate dateTo;

    @Schema(description = "匯出格式 (EXCEL)", example = "EXCEL")
    private ExportFormat format = ExportFormat.EXCEL;

    @Schema(description = "報表類型 (SUMMARY/DETAIL)", example = "SUMMARY")
    private ReportType reportType = ReportType.SUMMARY;

    /**
     * 匯出格式
     */
    public enum ExportFormat {
        EXCEL
    }

    /**
     * 報表類型
     */
    public enum ReportType {
        SUMMARY, DETAIL
    }
}
