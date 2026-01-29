package com.company.hrms.document.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.document.api.request.DeleteDocumentRequest;
import com.company.hrms.document.api.request.GenerateDocumentRequest;
import com.company.hrms.document.api.request.UploadDocumentRequest;
import com.company.hrms.document.api.response.DocumentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 文件管理命令控制器
 */
@Tag(name = "Document Command", description = "文件管理命令 API")
@RestController
@RequestMapping("/api/v1/documents")
public class HR13DocumentCmdController extends CommandBaseController {

    /**
     * 上傳文件
     */
    @Operation(summary = "上傳文件", operationId = "uploadDocument")
    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @Valid @RequestBody UploadDocumentRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 從範本產生文件
     */
    @Operation(summary = "從範本產生文件", operationId = "generateDocument")
    @PostMapping("/generate")
    public ResponseEntity<DocumentResponse> generateDocument(
            @Valid @RequestBody GenerateDocumentRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 刪除文件
     */
    @Operation(summary = "刪除文件", operationId = "deleteDocument")
    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<DocumentResponse> deleteDocument(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity
                .ok(execCommand(new DeleteDocumentRequest(id), currentUser));
    }
}
