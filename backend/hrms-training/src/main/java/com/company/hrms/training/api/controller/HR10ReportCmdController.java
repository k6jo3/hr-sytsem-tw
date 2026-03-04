package com.company.hrms.training.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import com.company.hrms.common.annotation.CurrentUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.training.api.request.ExportReportRequest;
import com.company.hrms.training.api.response.FileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/training/reports")
@Tag(name = "HR10 - Reporting", description = "訓練報表 (Command)")
@RequiredArgsConstructor
public class HR10ReportCmdController extends CommandBaseController {

    @PostMapping("/export")
    @Operation(summary = "匯出訓練報表", operationId = "exportTrainingReport")
    public ResponseEntity<FileResponse> exportTrainingReport(
            @CurrentUser JWTModel currentUser,
            @RequestBody ExportReportRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
