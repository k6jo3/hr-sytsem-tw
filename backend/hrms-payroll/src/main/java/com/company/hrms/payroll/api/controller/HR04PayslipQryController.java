package com.company.hrms.payroll.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.GetPayslipListRequest;
import com.company.hrms.payroll.application.dto.response.PayslipResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 薪資單查詢 Controller
 * 處理薪資單的查詢操作
 */
@RestController
@RequestMapping("/api/v1/payslips")
@Slf4j
@Tag(name = "HR04-Payslip", description = "薪資單管理")
public class HR04PayslipQryController extends QueryBaseController {

    /**
     * 查詢薪資單列表
     * Service Bean: getPayslipListServiceImpl
     */
    @GetMapping
    @Operation(summary = "查詢薪資單列表", operationId = "getPayslipList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    public ResponseEntity<PageResponse<PayslipResponse>> getPayslipList(
            @ModelAttribute GetPayslipListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    /**
     * 查詢單一薪資單
     * Service Bean: getPayslipByIdServiceImpl
     */
    @GetMapping("/{payslipId}")
    @Operation(summary = "查詢薪資單詳情", operationId = "getPayslipById")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查詢成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限查看此薪資單"),
            @ApiResponse(responseCode = "404", description = "薪資單不存在")
    })
    public ResponseEntity<PayslipResponse> getPayslipById(
            @PathVariable String payslipId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(payslipId, currentUser, payslipId));
    }

    /**
     * 取得薪資單 PDF
     * Service Bean: generatePayslipPdfServiceImpl
     *
     * 回傳含 pdfUrl 的 PayslipResponse，前端另行下載 PDF
     */
    @GetMapping("/{payslipId}/pdf")
    @Operation(summary = "取得薪資單 PDF", operationId = "generatePayslipPdf")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得 PDF URL"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "403", description = "無權限下載此薪資單"),
            @ApiResponse(responseCode = "404", description = "薪資單不存在")
    })
    public ResponseEntity<PayslipResponse> generatePayslipPdf(
            @PathVariable String payslipId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(payslipId, currentUser, payslipId));
    }
}
