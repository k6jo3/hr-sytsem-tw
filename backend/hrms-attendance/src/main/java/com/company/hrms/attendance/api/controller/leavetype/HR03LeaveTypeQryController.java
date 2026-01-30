package com.company.hrms.attendance.api.controller.leavetype;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.leavetype.GetLeaveTypeListRequest;
import com.company.hrms.attendance.api.response.leavetype.LeaveTypeListResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR03 - 假別管理 Query Controller
 * 負責假別的查詢操作
 */
@RestController
@RequestMapping("/api/v1/leave/types")
@Tag(name = "HR03-LeaveType-Query", description = "假別管理查詢操作")
public class HR03LeaveTypeQryController extends QueryBaseController {

    /**
     * 查詢假別列表
     */
    @Operation(summary = "查詢假別列表", operationId = "getLeaveTypeList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @GetMapping
    public ResponseEntity<List<LeaveTypeListResponse>> getLeaveTypeList(
            GetLeaveTypeListRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
