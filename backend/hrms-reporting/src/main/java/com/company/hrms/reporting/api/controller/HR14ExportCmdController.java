package com.company.hrms.reporting.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.ExportExcelRequest;
import com.company.hrms.reporting.api.request.ExportGovernmentFormatRequest;
import com.company.hrms.reporting.api.request.ExportPdfRequest;
import com.company.hrms.reporting.api.response.ExportFileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * HR14 報表匯出 Command Controller
 * 負責處理報表匯出請求（非同步任務建立）
 */
@RestController
@RequestMapping("/api/v1/reporting/export")
@RequiredArgsConstructor
@Tag(name = "HR14-報表匯出(Cmd)", description = "建立報表匯出任務")
public class HR14ExportCmdController extends CommandBaseController {

    @PostMapping("/excel")
    @Operation(summary = "匯出 Excel 報表", operationId = "exportExcel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "匯出任務已建立"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<ExportFileResponse> exportExcel(
            @RequestBody ExportExcelRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        // 對應 Service: exportExcelServiceImpl (需實作 CommandApiService)
        ExportFileResponse response = (ExportFileResponse) execCommand(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/pdf")
    @Operation(summary = "匯出 PDF 報表", operationId = "exportPdf")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "匯出任務已建立"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<ExportFileResponse> exportPdf(
            @RequestBody ExportPdfRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        // 對應 Service: exportPdfServiceImpl (需實作 CommandApiService)
        ExportFileResponse response = (ExportFileResponse) execCommand(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/government")
    @Operation(summary = "政府申報格式匯出", operationId = "exportGovernmentFormat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "匯出任務已建立"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<ExportFileResponse> exportGovernmentFormat(
            @RequestBody ExportGovernmentFormatRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        // 對應 Service: exportGovernmentFormatServiceImpl (需實作 CommandApiService)
        ExportFileResponse response = (ExportFileResponse) execCommand(request, currentUser);
        return ResponseEntity.ok(response);
    }
}
