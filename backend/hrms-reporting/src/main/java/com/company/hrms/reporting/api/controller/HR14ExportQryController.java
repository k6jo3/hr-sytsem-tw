package com.company.hrms.reporting.api.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.reporting.api.request.DownloadExportFileRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * HR14 報表匯出 Query Controller
 * 負責處理檔案下載請求
 */
@RestController
@RequestMapping("/api/v1/reporting/export")
@RequiredArgsConstructor
@Tag(name = "HR14-報表匯出(Qry)", description = "下載匯出檔案")
@SuppressWarnings("null")
public class HR14ExportQryController extends QueryBaseController {

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
