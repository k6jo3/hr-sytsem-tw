package com.company.hrms.organization.api.controller.contract;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.security.CurrentUser;
import com.company.hrms.organization.api.response.contract.ContractListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 合約管理查詢控制器
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR02-Contract-Query", description = "合約管理查詢操作")
public class HR02ContractQryController extends QueryBaseController {

    @GetMapping("/api/v1/employees/{employeeId}/contracts")
    @Operation(summary = "查詢員工合約清單", operationId = "getContractList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "404", description = "員工不存在")
    })
    public ResponseEntity<ContractListResponse> getContractList(
            @PathVariable String employeeId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Getting contracts for employee: {}", employeeId);
        return ResponseEntity.ok(getResponse(null, currentUser, employeeId));
    }

    @GetMapping("/api/v1/contracts/expiring")
    @Operation(summary = "查詢即將到期合約", operationId = "getExpiringContracts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功")
    })
    public ResponseEntity<ContractListResponse> getExpiringContracts(
            @RequestParam(defaultValue = "30") Integer days,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Getting expiring contracts within {} days", days);
        return ResponseEntity.ok(getResponse(null, currentUser, String.valueOf(days)));
    }
}
