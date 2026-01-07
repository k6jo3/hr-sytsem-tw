package com.company.hrms.insurance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.api.request.ExportEnrollmentReportRequest;
import com.company.hrms.insurance.api.response.ExportEnrollmentReportResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR05 申報檔案匯出 Command Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/export")
@Tag(name = "HR05-Export", description = "申報檔案匯出")
public class HR05ExportCmdController extends CommandBaseController {

    @PostMapping("/enrollment-report")
    @Operation(summary = "匯出加退保申報檔", operationId = "exportEnrollmentReport")
    public ResponseEntity<ExportEnrollmentReportResponse> exportEnrollmentReport(
            @RequestBody ExportEnrollmentReportRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
