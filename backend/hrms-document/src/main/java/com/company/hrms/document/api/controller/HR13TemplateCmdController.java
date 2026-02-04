package com.company.hrms.document.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.document.api.request.CreateDocumentTemplateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 文件範本命令控制器
 */
@Tag(name = "Document Template Command", description = "文件範本命令 API")
@RestController
@RequestMapping("/api/v1/documents/templates")
public class HR13TemplateCmdController extends CommandBaseController {

    /**
     * 建立文件範本
     */
    @Operation(summary = "建立文件範本", operationId = "createDocumentTemplate")
    @PostMapping
    public ResponseEntity<String> createDocumentTemplate(
            @Valid @RequestBody CreateDocumentTemplateRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 更新文件範本
     */
    @Operation(summary = "更新文件範本", operationId = "updateDocumentTemplate")
    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public ResponseEntity<Void> updateDocumentTemplate(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @Valid @RequestBody com.company.hrms.document.api.request.UpdateDocumentTemplateRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        request.setId(id);
        execCommand(request, currentUser);
        return ResponseEntity.ok().build();
    }

    /**
     * 刪除文件範本
     */
    @Operation(summary = "刪除文件範本", operationId = "deleteDocumentTemplate")
    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumentTemplate(
            @org.springframework.web.bind.annotation.PathVariable String id,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(id, currentUser);
        return ResponseEntity.ok().build();
    }
}
