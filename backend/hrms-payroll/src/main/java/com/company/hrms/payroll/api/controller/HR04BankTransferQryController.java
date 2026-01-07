package com.company.hrms.payroll.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 銀行薪轉查詢 Controller
 * 處理銀行薪轉檔案的下載操作
 */
@RestController
@RequestMapping("/api/v1/payroll-runs/{runId}/bank-transfer")
@Slf4j
@Tag(name = "HR04-BankTransfer", description = "銀行薪轉管理")
public class HR04BankTransferQryController extends QueryBaseController {

    /**
     * 下載銀行薪轉檔案
     * Service Bean: downloadBankTransferFileServiceImpl
     *
     * 回傳該薪資批次的薪轉檔案下載 URL
     */
    @GetMapping("/download")
    @Operation(summary = "下載銀行薪轉檔案", operationId = "downloadBankTransferFile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取得下載 URL 成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "批次或檔案不存在")
    })
    public ResponseEntity<String> downloadBankTransferFile(
            @PathVariable String runId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(runId, currentUser, runId));
    }
}
