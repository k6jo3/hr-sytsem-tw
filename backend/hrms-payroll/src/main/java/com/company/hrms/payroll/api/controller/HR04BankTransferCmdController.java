package com.company.hrms.payroll.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 銀行薪轉命令 Controller
 * 處理銀行薪轉檔案的產生操作
 */
@RestController
@RequestMapping("/api/v1/payroll-runs/{runId}/bank-transfer")
@Slf4j
@Tag(name = "HR04-BankTransfer", description = "銀行薪轉管理")
public class HR04BankTransferCmdController extends CommandBaseController {

    /**
     * 產生銀行薪轉檔案
     * Service Bean: generateBankTransferFileServiceImpl
     *
     * 產生該薪資批次的銀行薪轉檔案 (CSV/TXT 格式)
     */
    @PostMapping
    @Operation(summary = "產生銀行薪轉檔案", operationId = "generateBankTransferFile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "產生成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "批次不存在"),
            @ApiResponse(responseCode = "409", description = "批次狀態不允許產生薪轉檔")
    })
    public ResponseEntity<PayrollRunResponse> generateBankTransferFile(
            @PathVariable String runId,
            @RequestBody(required = false) PayrollRunActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        PayrollRunActionRequest req = request != null ? request : new PayrollRunActionRequest();
        req.setRunId(runId);
        return ResponseEntity.ok(execCommand(req, currentUser, runId));
    }
}
