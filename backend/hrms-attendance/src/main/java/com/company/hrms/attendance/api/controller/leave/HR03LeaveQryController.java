package com.company.hrms.attendance.api.controller.leave;

import com.company.hrms.attendance.api.response.leave.LeaveApplicationDetailResponse;
import com.company.hrms.attendance.api.response.leave.LeaveApplicationListResponse;
import com.company.hrms.attendance.api.response.leave.LeaveBalanceResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * HR03 - 請假管理 Query Controller
 * 負責請假申請與假別餘額的查詢操作
 */
@RestController
@RequestMapping("/api/v1/leave")
@Tag(name = "HR03-Leave-Query", description = "請假管理查詢操作")
public class HR03LeaveQryController extends QueryBaseController {

    /**
     * 查詢請假申請列表
     */
    @Operation(summary = "查詢請假申請列表", operationId = "getLeaveApplications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @GetMapping("/applications")
    public ResponseEntity<List<LeaveApplicationListResponse>> getLeaveApplications(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String leaveTypeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        LeaveApplicationQueryRequest request = new LeaveApplicationQueryRequest(
                employeeId, departmentId, leaveTypeId, status, startDate, endDate);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    /**
     * 查詢單筆請假申請
     */
    @Operation(summary = "查詢單筆請假申請", operationId = "getLeaveApplication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "申請不存在")
    })
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<LeaveApplicationDetailResponse> getLeaveApplication(
            @PathVariable String applicationId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(new GetLeaveApplicationRequest(), currentUser, applicationId));
    }

    /**
     * 查詢員工假別餘額
     */
    @Operation(summary = "查詢員工假別餘額", operationId = "getLeaveBalance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "員工不存在")
    })
    @GetMapping("/balances/{employeeId}")
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalance(
            @PathVariable String employeeId,
            @RequestParam(required = false) Integer year,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        LeaveBalanceQueryRequest request = new LeaveBalanceQueryRequest(year);
        return ResponseEntity.ok(getResponse(request, currentUser, employeeId));
    }

    /**
     * 請假申請查詢請求 (內部類別)
     */
    public record LeaveApplicationQueryRequest(
            String employeeId,
            String departmentId,
            String leaveTypeId,
            String status,
            LocalDate startDate,
            LocalDate endDate) {}

    /**
     * 取得請假申請請求 (內部類別)
     */
    public record GetLeaveApplicationRequest() {}

    /**
     * 假別餘額查詢請求 (內部類別)
     */
    public record LeaveBalanceQueryRequest(Integer year) {}
}
