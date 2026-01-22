package com.company.hrms.training.api.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "匯出報表請求")
public class ExportReportRequest {

    @Schema(description = "開始日期")
    private LocalDate startDate;

    @Schema(description = "結束日期")
    private LocalDate endDate;

    @Schema(description = "報表類型", example = "ENROLLMENT_SUMMARY")
    private String reportType;

    @Schema(description = "格式", example = "EXCEL, PDF")
    private String format;
}
