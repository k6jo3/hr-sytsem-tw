package com.company.hrms.training.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.company.hrms.common.annotation.CurrentUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.training.api.request.GetCertificatesRequest;
import com.company.hrms.training.api.response.CertificateResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 證照管理查詢 Controller
 * 使用 Request DTO 進行宣告式查詢
 */
@RestController
@RequestMapping("/api/v1/training/certificates")
@Tag(name = "HR10 - Certificate Management", description = "證照管理 (Query)")
@RequiredArgsConstructor
public class HR10CertificateQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢證照列表", operationId = "getCertificates")
    public ResponseEntity<Page<CertificateResponse>> getCertificates(
            @CurrentUser JWTModel currentUser,
            GetCertificatesRequest request) throws Exception {
        if (request == null) {
            request = new GetCertificatesRequest();
        }
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @GetMapping("/{certificateId}")
    @Operation(summary = "查詢證照詳情", operationId = "getCertificateDetail")
    public ResponseEntity<CertificateResponse> getCertificateDetail(
            @CurrentUser JWTModel currentUser,
            @PathVariable String certificateId) throws Exception {
        return ResponseEntity.ok(getResponse(certificateId, currentUser));
    }

    @GetMapping("/expiring")
    @Operation(summary = "查詢即將到期證照", operationId = "getExpiringCertificates")
    public ResponseEntity<List<CertificateResponse>> getExpiringCertificates(
            @CurrentUser JWTModel currentUser,
            @Parameter(description = "到期天數") @RequestParam(defaultValue = "90") Integer days) throws Exception {
        // 即將到期的查詢使用專用 Service
        return ResponseEntity.ok(getResponse(new QueryGroup(), currentUser));
    }
}
