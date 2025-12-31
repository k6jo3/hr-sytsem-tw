package com.company.hrms.attendance.api.controller.overtime;

import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationDetailResponse;
import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationListResponse;
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
 * HR03 - 加班管理 Query Controller
 * 負責加班申請的查詢操作
 */
@RestController
@RequestMapping("/api/v1/overtime")
@Tag(name = "HR03-Overtime-Query", description = "加班管理查詢操作")
public class HR03OvertimeQryController extends QueryBaseController {

    /**
     * 查詢加班申請列表
     */
    @Operation(summary = "查詢加班申請列表", operationId = "getOvertimeApplications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @GetMapping("/applications")
    public ResponseEntity<List<OvertimeApplicationListResponse>> getOvertimeApplications(
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String overtimeType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        OvertimeApplicationQueryRequest request = new OvertimeApplicationQueryRequest(
                employeeId, departmentId, overtimeType, status, startDate, endDate);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    /**
     * 查詢單筆加班申請
     */
    @Operation(summary = "查詢單筆加班申請", operationId = "getOvertimeApplication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "申請不存在")
    })
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<OvertimeApplicationDetailResponse> getOvertimeApplication(
            @PathVariable String applicationId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(new GetOvertimeApplicationRequest(), currentUser, applicationId));
    }

    /**
     * 加班申請查詢請求 (內部類別)
     */
    public record OvertimeApplicationQueryRequest(
            String employeeId,
            String departmentId,
            String overtimeType,
            String status,
            LocalDate startDate,
            LocalDate endDate) {}

    /**
     * 取得加班申請請求 (內部類別)
     */
    public record GetOvertimeApplicationRequest() {}
}
