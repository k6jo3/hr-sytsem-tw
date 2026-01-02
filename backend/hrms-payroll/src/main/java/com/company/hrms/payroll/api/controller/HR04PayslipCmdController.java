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
import com.company.hrms.payroll.application.dto.response.PayslipResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 薪資單命令 Controller
 * 處理薪資單的發送操作
 */
@RestController
@RequestMapping("/api/v1/payroll-runs/{runId}")
@Slf4j
@Tag(name = "HR04-Payslip", description = "薪資單管理")
public class HR04PayslipCmdController extends CommandBaseController {

    /**
     * 發送薪資單 Email
     * Service Bean: sendPayslipEmailServiceImpl
     *
     * 批次發送該薪資批次下所有員工的薪資單 Email
     */
    @PostMapping("/send-payslips")
    @Operation(summary = "發送薪資單 Email", operationId = "sendPayslipEmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "發送成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "批次不存在"),
            @ApiResponse(responseCode = "409", description = "批次狀態不允許發送薪資單")
    })
    public ResponseEntity<PayslipResponse> sendPayslipEmail(
            @PathVariable String runId,
            @RequestBody(required = false) PayrollRunActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        PayrollRunActionRequest req = request != null ? request : new PayrollRunActionRequest();
        req.setRunId(runId);
        return ResponseEntity.ok(execCommand(req, currentUser, runId));
    }
}
