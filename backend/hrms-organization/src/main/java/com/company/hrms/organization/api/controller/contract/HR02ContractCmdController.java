package com.company.hrms.organization.api.controller.contract;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.organization.api.request.contract.CreateContractRequest;
import com.company.hrms.organization.api.request.contract.RenewContractRequest;
import com.company.hrms.organization.api.request.contract.UpdateContractRequest;
import com.company.hrms.organization.api.response.contract.ContractDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 合約管理寫入控制器
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR02-Contract-Command", description = "合約管理寫入操作")
public class HR02ContractCmdController extends CommandBaseController {

        @PostMapping("/api/v1/employees/{employeeId}/contracts")
        @Operation(summary = "新增合約", operationId = "createContract")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
                        @ApiResponse(responseCode = "404", description = "員工不存在")
        })
        public ResponseEntity<ContractDetailResponse> createContract(
                        @PathVariable String employeeId,
                        @Valid @RequestBody CreateContractRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Creating contract for employee: {}", employeeId);
                return ResponseEntity.ok(execCommand(request, currentUser, employeeId));
        }

        @PutMapping("/api/v1/contracts/{contractId}")
        @Operation(summary = "更新合約", operationId = "updateContract")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
                        @ApiResponse(responseCode = "404", description = "合約不存在")
        })
        public ResponseEntity<ContractDetailResponse> updateContract(
                        @PathVariable String contractId,
                        @Valid @RequestBody UpdateContractRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Updating contract: {}", contractId);
                return ResponseEntity.ok(execCommand(request, currentUser, contractId));
        }

        @PutMapping("/api/v1/contracts/{contractId}/renew")
        @Operation(summary = "續約", operationId = "renewContract")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "請求格式錯誤或合約不可續約"),
                        @ApiResponse(responseCode = "404", description = "合約不存在")
        })
        public ResponseEntity<ContractDetailResponse> renewContract(
                        @PathVariable String contractId,
                        @Valid @RequestBody RenewContractRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Renewing contract: {}", contractId);
                return ResponseEntity.ok(execCommand(request, currentUser, contractId));
        }

        @PutMapping("/api/v1/contracts/{contractId}/terminate")
        @Operation(summary = "終止合約", operationId = "terminateContract")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "404", description = "合約不存在")
        })
        public ResponseEntity<ContractDetailResponse> terminateContract(
                        @PathVariable String contractId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Terminating contract: {}", contractId);
                return ResponseEntity.ok(execCommand(null, currentUser, contractId));
        }
}
