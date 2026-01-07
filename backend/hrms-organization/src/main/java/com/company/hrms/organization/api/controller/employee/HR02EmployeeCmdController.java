package com.company.hrms.organization.api.controller.employee;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.organization.api.request.employee.*;
import com.company.hrms.organization.api.response.employee.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * HR02 - 員工管理 Command Controller
 * 負責員工的新增、修改、調動、升遷、離職等寫入操作
 *
 * <p>命名規範：HR{DD}{Screen}CmdController</p>
 * <p>DD = 02 (Organization Domain)</p>
 */
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "HR02-Employee-Command", description = "員工管理寫入操作")
public class HR02EmployeeCmdController extends CommandBaseController {

    /**
     * 新增員工
     */
    @Operation(summary = "新增員工", operationId = "createEmployee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "409", description = "員工編號或Email已存在")
    })
    @PostMapping
    public ResponseEntity<CreateEmployeeResponse> createEmployee(
            @RequestBody @Valid CreateEmployeeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 更新員工
     */
    @Operation(summary = "更新員工", operationId = "updateEmployee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "404", description = "員工不存在")
    })
    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDetailResponse> updateEmployee(
            @PathVariable String employeeId,
            @RequestBody @Valid UpdateEmployeeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, employeeId));
    }

    /**
     * 部門調動
     */
    @Operation(summary = "部門調動", operationId = "transferEmployee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "404", description = "員工不存在")
    })
    @PostMapping("/{employeeId}/transfer")
    public ResponseEntity<TransferEmployeeResponse> transferEmployee(
            @PathVariable String employeeId,
            @RequestBody @Valid TransferEmployeeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, employeeId));
    }

    /**
     * 升遷
     */
    @Operation(summary = "員工升遷", operationId = "promoteEmployee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "404", description = "員工不存在")
    })
    @PostMapping("/{employeeId}/promote")
    public ResponseEntity<PromoteEmployeeResponse> promoteEmployee(
            @PathVariable String employeeId,
            @RequestBody @Valid PromoteEmployeeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, employeeId));
    }

    /**
     * 調薪
     */
    @Operation(summary = "員工調薪", operationId = "adjustSalary")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "成功"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "404", description = "員工不存在")
    })
    @PostMapping("/{employeeId}/adjust-salary")
    public ResponseEntity<Void> adjustSalary(
            @PathVariable String employeeId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, employeeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 離職
     */
    @Operation(summary = "員工離職", operationId = "terminateEmployee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "404", description = "員工不存在")
    })
    @PostMapping("/{employeeId}/terminate")
    public ResponseEntity<TerminateEmployeeResponse> terminateEmployee(
            @PathVariable String employeeId,
            @RequestBody @Valid TerminateEmployeeRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, employeeId));
    }

    /**
     * 試用期轉正
     */
    @Operation(summary = "試用期轉正", operationId = "regularizeEmployee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "成功"),
        @ApiResponse(responseCode = "401", description = "未授權"),
        @ApiResponse(responseCode = "404", description = "員工不存在"),
        @ApiResponse(responseCode = "409", description = "員工不在試用期")
    })
    @PostMapping("/{employeeId}/regularize")
    public ResponseEntity<Void> regularizeEmployee(
            @PathVariable String employeeId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        execCommand(null, currentUser, employeeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批次匯入員工
     */
    @Operation(summary = "批次匯入員工", operationId = "importEmployees")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "400", description = "檔案格式錯誤"),
        @ApiResponse(responseCode = "401", description = "未授權")
    })
    @PostMapping("/import")
    public ResponseEntity<Void> importEmployees(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        // TODO: 實作 Excel 匯入
        return ResponseEntity.noContent().build();
    }
}
