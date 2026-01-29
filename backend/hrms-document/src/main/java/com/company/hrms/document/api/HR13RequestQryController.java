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
import com.company.hrms.document.api.request.GetDocumentRequestListRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 文件申請查詢控制器
 */
@Tag(name = "Document Request Query", description = "文件申請查詢 API")
@RestController
@RequestMapping("/api/v1/documents")
public class HR13RequestQryController extends QueryBaseController {

    /**
     * 可申請文件類型
     */
    @Operation(summary = "獲取可申請文件類型", operationId = "getDocumentRequestTypes")
    @GetMapping("/request-types")
    public ResponseEntity<java.util.List<Object>> getDocumentRequestTypes(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        // 使用 getResponse 調用 getDocumentRequestTypeListServiceImpl (待實作)
        return ResponseEntity.ok(getResponse(null, currentUser));
    }

    /**
     * 查詢文件申請歷史
     */
    @Operation(summary = "查詢文件申請歷史", operationId = "getDocumentRequestList")
    @GetMapping("/requests")
    public ResponseEntity<Page<Object>> getDocumentRequestList(
            GetDocumentRequestListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser,
            Pageable pageable) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser, pageable.toString()));
    }
}
