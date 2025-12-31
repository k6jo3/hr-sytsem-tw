package com.company.hrms.attendance.api.controller.shift;

import com.company.hrms.attendance.api.response.shift.ShiftListResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * HR03 - 班別管理 Query Controller
 * 負責班別的查詢操作
 */
@RestController
@RequestMapping("/api/v1/shifts")
@Tag(name = "HR03-Shift-Query", description = "班別管理查詢操作")
public class HR03ShiftQryController extends QueryBaseController {

    /**
     * 查詢班別列表
     */
    @Operation(summary = "查詢班別列表", operationId = "getShiftList")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @GetMapping
    public ResponseEntity<List<ShiftListResponse>> getShiftList(
            @RequestParam(required = false) String organizationId,
            @RequestParam(required = false) String shiftType,
            @RequestParam(required = false) Boolean isActive,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        ShiftQueryRequest request = new ShiftQueryRequest(organizationId, shiftType, isActive);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    /**
     * 班別查詢請求 (內部類別)
     */
    public record ShiftQueryRequest(String organizationId, String shiftType, Boolean isActive) {}
}
