package com.company.hrms.training.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.company.hrms.common.annotation.CurrentUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.training.api.request.AddCertificateRequest;
import com.company.hrms.training.api.request.UpdateCertificateRequest;
import com.company.hrms.training.api.response.CertificateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/training/certificates")
@Tag(name = "HR10 - Certificate Management", description = "證照管理 (Command)")
@RequiredArgsConstructor
public class HR10CertificateCmdController extends CommandBaseController {

    @PostMapping
    @Operation(summary = "新增證照", operationId = "addCertificate")
    public ResponseEntity<CertificateResponse> addCertificate(
            @CurrentUser JWTModel currentUser,
            @RequestBody @org.springframework.validation.annotation.Validated({
                    Default.class }) AddCertificateRequest request)
            throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PutMapping("/{certificateId}")
    @Operation(summary = "更新證照", operationId = "updateCertificate")
    public ResponseEntity<CertificateResponse> updateCertificate(
            @CurrentUser JWTModel currentUser,
            @PathVariable String certificateId,
            @RequestBody @Valid UpdateCertificateRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, certificateId));
    }

    @DeleteMapping("/{certificateId}")
    @Operation(summary = "刪除證照", operationId = "deleteCertificate")
    public ResponseEntity<Void> deleteCertificate(
            @CurrentUser JWTModel currentUser,
            @PathVariable String certificateId) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, certificateId));
    }
}
