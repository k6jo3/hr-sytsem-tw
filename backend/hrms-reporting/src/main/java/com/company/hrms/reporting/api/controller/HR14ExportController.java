package com.company.hrms.reporting.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.ExportExcelRequest;
import com.company.hrms.reporting.application.service.export.ExcelExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * HR14 報表匯出 Controller
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/v1/reporting/export")
@RequiredArgsConstructor
@Tag(name = "HR14-報表匯出", description = "報表匯出功能 (Excel/PDF)")
public class HR14ExportController {

    private final ExcelExportService excelExportService;

    @PostMapping("/excel")
    @Operation(summary = "匯出 Excel 報表", operationId = "exportExcel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "匯出成功"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<byte[]> exportExcel(
            @RequestBody ExportExcelRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        // TODO: 根據 request.getReportType() 查詢資料並轉換為 Excel
        // 目前僅實作員工花名冊的模擬資料匯出作為範例

        byte[] excelContent;
        String filename = request.getFileName() != null ? request.getFileName() : "report.xlsx";
        if (!filename.endsWith(".xlsx")) {
            filename += ".xlsx";
        }

        if ("EMPLOYEE_ROSTER".equals(request.getReportType())) {
            // 模擬資料
            java.util.List<ExcelExportService.EmployeeRosterData> mockData = java.util.List.of(
                    ExcelExportService.EmployeeRosterData.builder()
                            .employeeId("EMP001")
                            .name("王小明")
                            .departmentName("資訊部")
                            .positionName("工程師")
                            .hireDate("2023-01-01")
                            .serviceYears(3.0)
                            .status("ACTIVE")
                            .phone("0912345678")
                            .email("wang@company.com")
                            .build(),
                    ExcelExportService.EmployeeRosterData.builder()
                            .employeeId("EMP002")
                            .name("李大華")
                            .departmentName("業務部")
                            .positionName("經理")
                            .hireDate("2020-05-20")
                            .serviceYears(5.6)
                            .status("ACTIVE")
                            .phone("0987654321")
                            .email("lee@company.com")
                            .build());
            excelContent = excelExportService.exportEmployeeRoster(mockData);
        } else {
            // 預設空的 Excel
            excelContent = excelExportService.exportToExcel(
                    java.util.List.of("訊息"),
                    java.util.List.of(java.util.List.of("尚未支援此報表類型: " + request.getReportType())),
                    "錯誤");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="
                                + java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8))
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }
}
