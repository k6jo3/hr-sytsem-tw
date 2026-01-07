package com.company.hrms.attendance.api.controller.attendance;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.attendance.CheckInRequest;
import com.company.hrms.attendance.api.request.attendance.CheckOutRequest;
import com.company.hrms.attendance.api.request.attendance.CreateCorrectionRequest;
import com.company.hrms.attendance.api.request.attendance.ApproveCorrectionRequest;
import com.company.hrms.attendance.api.response.attendance.CheckInResponse;
import com.company.hrms.attendance.api.response.attendance.CheckOutResponse;
import com.company.hrms.attendance.api.response.attendance.CreateCorrectionResponse;
import com.company.hrms.attendance.api.response.attendance.ApproveCorrectionResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * HR03 - 打卡管理 Command Controller
 * 負責上班打卡、下班打卡、補卡申請等寫入操作
 *
 * <p>
 * 命名規範：HR{DD}{Screen}CmdController
 * </p>
 * <p>
 * DD = 03 (Attendance Domain)
 * </p>
 */
@RestController
@RequestMapping("/api/v1/attendance")
@Tag(name = "HR03-CheckIn-Command", description = "打卡管理寫入操作")
public class HR03CheckInCmdController extends CommandBaseController {

    /**
     * 上班打卡
     */
    @Operation(summary = "上班打卡", operationId = "checkIn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "409", description = "今日已完成上班打卡")
    })
    @PostMapping("/check-in")
    public ResponseEntity<CheckInResponse> checkIn(
            @RequestBody @Valid CheckInRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 下班打卡
     */
    @Operation(summary = "下班打卡", operationId = "checkOut")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "409", description = "今日尚未上班打卡或已完成下班打卡")
    })
    @PostMapping("/check-out")
    public ResponseEntity<CheckOutResponse> checkOut(
            @RequestBody @Valid CheckOutRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 提交補卡申請
     */
    @Operation(summary = "提交補卡申請", operationId = "createCorrection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤或超過補卡期限"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "該日無打卡記錄")
    })
    @PostMapping("/corrections")
    public ResponseEntity<CreateCorrectionResponse> createCorrection(
            @RequestBody @Valid CreateCorrectionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 審核補卡申請
     */
    @Operation(summary = "審核補卡申請", operationId = "approveCorrection")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "補卡申請不存在")
    })
    @PutMapping("/corrections/{correctionId}/approve")
    public ResponseEntity<ApproveCorrectionResponse> approveCorrection(
            @PathVariable String correctionId,
            @RequestBody(required = false) ApproveCorrectionRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        if (request == null) {
            request = new ApproveCorrectionRequest();
        }
        return ResponseEntity.ok(execCommand(request, currentUser, correctionId));
    }
}
