package com.company.hrms.document.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.document.api.request.SubmitDocumentRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 文件申請命令控制器
 */
@Tag(name = "Document Request Command", description = "文件申請命令 API")
@RestController
@RequestMapping("/api/v1/documents/request")
public class HR13RequestCmdController extends CommandBaseController {

    /**
     * 提交文件申請
     */
    @Operation(summary = "提交文件申請", operationId = "submitDocumentRequest")
    @PostMapping
    public ResponseEntity<String> submitDocumentRequest(
            @Valid @RequestBody SubmitDocumentRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
