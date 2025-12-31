package com.company.hrms.attendance.api.controller.monthclose;

import com.company.hrms.attendance.api.request.monthclose.ExecuteMonthCloseRequest;
import com.company.hrms.attendance.api.response.monthclose.ExecuteMonthCloseResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HR03 - 月結管理 Command Controller
 * 負責考勤月結的執行操作
 */
@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "HR03-MonthClose-Command", description = "考勤月結寫入操作")
public class HR03MonthCloseCmdController extends CommandBaseController {

    /**
     * 執行月結
     */
    @Operation(summary = "執行考勤月結", operationId = "executeMonthClose")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "409", description = "月結作業進行中或已完成")
    })
    @PostMapping("/month-close")
    public ResponseEntity<ExecuteMonthCloseResponse> executeMonthClose(
            @RequestBody @Valid ExecuteMonthCloseRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
