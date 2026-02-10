package com.company.hrms.reporting.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.DownloadExportFileRequest;
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
 * HR14 報表匯出 Controller
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@RestController
@RequestMapping("/api/v1/reporting/export")
@RequiredArgsConstructor
@Tag(name = "HR14-報表匯出", description = "報表匯出功能 (Excel/PDF)")
public class HR14ExportController extends QueryBaseController {

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

                // 遵循架構模式：Controller 只做編排，不包含具體決策與邏輯
                // getResponse 會自動查找名為 exportExcelServiceImpl 的 Bean
                byte[] excelContent = getResponse(request, currentUser);

                String filename = request.getFileName() != null ? request.getFileName() : "report.xlsx";
                if (!filename.endsWith(".xlsx")) {
                        filename += ".xlsx";
                }

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename="
                                                                + java.net.URLEncoder.encode(filename,
                                                                                java.nio.charset.StandardCharsets.UTF_8))
                                .contentType(
                                                MediaType.parseMediaType(
                                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                                .body(excelContent);
        }

        @PostMapping("/pdf")
        @Operation(summary = "匯出 PDF 報表", operationId = "exportPdf")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "匯出成功"),
                        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        public ResponseEntity<byte[]> exportPdf(
                        @RequestBody ExportPdfRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                // Controller 只做編排，自動查找 exportPdfServiceImpl
                byte[] pdfContent = getResponse(request, currentUser);

                String filename = request.getFileName() != null ? request.getFileName() : "report.pdf";
                if (!filename.endsWith(".pdf")) {
                        filename += ".pdf";
                }

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename="
                                                                + java.net.URLEncoder.encode(filename,
                                                                                java.nio.charset.StandardCharsets.UTF_8))
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(pdfContent);
        }

        @PostMapping("/government")
        @Operation(summary = "政府申報格式匯出", operationId = "exportGovernmentFormat")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "匯出成功"),
                        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        public ResponseEntity<ExportFileResponse> exportGovernmentFormat(
                        @RequestBody ExportGovernmentFormatRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                // Controller 只做編排，自動查找 exportGovernmentFormatServiceImpl
                ExportFileResponse response = getResponse(request, currentUser);

                return ResponseEntity.ok(response);
        }

        @GetMapping("/{exportId}/download")
        @Operation(summary = "下載匯出檔案", operationId = "downloadExportFile")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "下載成功"),
                        @ApiResponse(responseCode = "404", description = "檔案不存在"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        public ResponseEntity<byte[]> downloadExportFile(
                        @PathVariable String exportId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                DownloadExportFileRequest request = new DownloadExportFileRequest();
                request.setExportId(exportId);

                // Controller 只做編排，自動查找 downloadExportFileServiceImpl
                byte[] fileContent = getResponse(request, currentUser);

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                "attachment; filename=" + exportId)
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(fileContent);
        }
}
