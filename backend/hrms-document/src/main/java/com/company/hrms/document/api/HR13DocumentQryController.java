package com.company.hrms.document.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.document.api.request.GetDocumentListRequest;
import com.company.hrms.document.api.response.DocumentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 文件管理查詢控制器
 */
@Tag(name = "Document Query", description = "文件管理查詢 API")
@RestController
@RequestMapping("/api/v1/documents")
public class HR13DocumentQryController extends QueryBaseController {

    /**
     * 查詢文件列表
     */
    @Operation(summary = "查詢文件列表", operationId = "getDocumentList")
    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> getDocumentList(
            GetDocumentListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser,
            Pageable pageable) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser, pageable.toString()));
    }

    /**
     * 我的文件 (ESS)
     */
    @Operation(summary = "查詢我的文件", operationId = "getMyDocuments")
    @GetMapping("/my")
    public ResponseEntity<Page<DocumentResponse>> getMyDocuments(
            GetDocumentListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser,
            Pageable pageable) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser, pageable.toString()));
    }

    /**
     * 獲取文件詳情
     */
    @Operation(summary = "獲取文件詳情", operationId = "getDocumentDetail")
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentDetail(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(id, currentUser));
    }

    /**
     * 下載文件
     */
    @Operation(summary = "下載文件", operationId = "downloadDocument")
    @GetMapping("/{id}/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadDocument(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        com.company.hrms.document.api.response.FileDownloadResponse response = getResponse(id, currentUser);

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + response.getFileName() + "\"")
                .contentType(org.springframework.http.MediaType.parseMediaType(response.getMimeType()))
                .body(new org.springframework.core.io.ByteArrayResource(response.getContent()));
    }

    /**
     * 獲取文件版本歷史
     */
    @Operation(summary = "獲取文件版本歷史", operationId = "getDocumentVersions")
    @GetMapping("/{id}/versions")
    public ResponseEntity<com.company.hrms.document.api.response.DocumentVersionResponse> getDocumentVersions(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(id, currentUser));
    }

    /**
     * 查詢下載紀錄 (稽核)
     */
    @Operation(summary = "查詢下載紀錄", operationId = "getDocumentAccessLogList")
    @GetMapping("/download-logs")
    public ResponseEntity<Page<Object>> getDocumentAccessLogList(
            com.company.hrms.document.api.request.GetDocumentAccessLogListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser,
            Pageable pageable) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser, pageable.toString()));
    }
}
