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
import com.company.hrms.document.api.request.GetDocumentTemplateListRequest;
import com.company.hrms.document.domain.model.DocumentTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 文件範本查詢控制器
 */
@Tag(name = "Document Template Query", description = "文件範本查詢 API")
@RestController
@RequestMapping("/api/v1/documents/templates")
public class HR13TemplateQryController extends QueryBaseController {

    /**
     * 查詢文件範本列表
     */
    @Operation(summary = "查詢文件範本列表", operationId = "getDocumentTemplateList")
    @GetMapping
    public ResponseEntity<Page<DocumentTemplate>> getDocumentTemplateList(
            GetDocumentTemplateListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser,
            Pageable pageable) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser, pageable.toString()));
    }

    /**
     * 獲取文件範本詳情
     */
    @Operation(summary = "獲取文件範本詳情", operationId = "getDocumentTemplateDetail")
    @GetMapping("/{id}")
    public ResponseEntity<DocumentTemplate> getDocumentTemplateDetail(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(id, currentUser));
    }
}
