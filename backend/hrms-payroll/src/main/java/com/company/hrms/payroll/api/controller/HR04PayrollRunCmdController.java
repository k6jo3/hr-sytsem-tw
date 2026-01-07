package com.company.hrms.payroll.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.CalculatePayrollRequest;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.dto.request.StartPayrollRunRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 薪資批次命令 Controller
 * 處理薪資批次的建立、執行、審核等操作
 */
@RestController
@RequestMapping("/api/v1/payroll-runs")
@Slf4j
@Tag(name = "HR04-PayrollRun", description = "薪資批次管理")
public class HR04PayrollRunCmdController extends CommandBaseController {

    /**
     * 建立薪資批次
     * Service Bean: startPayrollRunServiceImpl
     */
    @PostMapping
    @Operation(summary = "建立薪資批次", operationId = "startPayrollRun")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "建立成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "409", description = "該期間已有進行中的批次")
    })
    public ResponseEntity<PayrollRunResponse> startPayrollRun(
            @Valid @RequestBody StartPayrollRunRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 執行薪資計算
     * Service Bean: calculatePayrollServiceImpl
     */
    @PostMapping("/{runId}/execute")
    @Operation(summary = "執行薪資計算", operationId = "calculatePayroll")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "計算完成"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "批次不存在"),
            @ApiResponse(responseCode = "409", description = "批次狀態不允許執行計算")
    })
    public ResponseEntity<PayrollRunResponse> calculatePayroll(
            @PathVariable String runId,
            @RequestBody(required = false) CalculatePayrollRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        CalculatePayrollRequest req = request != null ? request : new CalculatePayrollRequest();
        req.setRunId(runId);
        return ResponseEntity.ok(execCommand(req, currentUser, runId));
    }

    /**
     * 送審薪資批次
     * Service Bean: submitPayrollRunServiceImpl
     */
    @PutMapping("/{runId}/submit")
    @Operation(summary = "送審薪資批次", operationId = "submitPayrollRun")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "送審成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "批次不存在"),
            @ApiResponse(responseCode = "409", description = "批次狀態不允許送審")
    })
    public ResponseEntity<PayrollRunResponse> submitPayrollRun(
            @PathVariable String runId,
            @RequestBody(required = false) PayrollRunActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        PayrollRunActionRequest req = request != null ? request : new PayrollRunActionRequest();
        req.setRunId(runId);
        return ResponseEntity.ok(execCommand(req, currentUser, runId));
    }

    /**
     * 核准薪資批次
     * Service Bean: approvePayrollRunServiceImpl
     */
    @PutMapping("/{runId}/approve")
    @Operation(summary = "核准薪資批次", operationId = "approvePayrollRun")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "核准成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限核准"),
            @ApiResponse(responseCode = "404", description = "批次不存在"),
            @ApiResponse(responseCode = "409", description = "批次狀態不允許核准")
    })
    public ResponseEntity<PayrollRunResponse> approvePayrollRun(
            @PathVariable String runId,
            @RequestBody(required = false) PayrollRunActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        PayrollRunActionRequest req = request != null ? request : new PayrollRunActionRequest();
        req.setRunId(runId);
        return ResponseEntity.ok(execCommand(req, currentUser, runId));
    }

    /**
     * 退回薪資批次
     * Service Bean: rejectPayrollRunServiceImpl
     */
    @PutMapping("/{runId}/reject")
    @Operation(summary = "退回薪資批次", operationId = "rejectPayrollRun")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "退回成功"),
            @ApiResponse(responseCode = "400", description = "退回原因為必填"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "批次不存在"),
            @ApiResponse(responseCode = "409", description = "批次狀態不允許退回")
    })
    public ResponseEntity<PayrollRunResponse> rejectPayrollRun(
            @PathVariable String runId,
            @Valid @RequestBody PayrollRunActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        request.setRunId(runId);
        return ResponseEntity.ok(execCommand(request, currentUser, runId));
    }

    /**
     * 標記已發薪
     * Service Bean: markPayrollRunPaidServiceImpl
     */
    @PutMapping("/{runId}/pay")
    @Operation(summary = "標記已發薪", operationId = "markPayrollRunPaid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "標記成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "批次不存在"),
            @ApiResponse(responseCode = "409", description = "批次狀態不允許標記發薪")
    })
    public ResponseEntity<PayrollRunResponse> markPayrollRunPaid(
            @PathVariable String runId,
            @RequestBody(required = false) PayrollRunActionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        PayrollRunActionRequest req = request != null ? request : new PayrollRunActionRequest();
        req.setRunId(runId);
        return ResponseEntity.ok(execCommand(req, currentUser, runId));
    }
}
