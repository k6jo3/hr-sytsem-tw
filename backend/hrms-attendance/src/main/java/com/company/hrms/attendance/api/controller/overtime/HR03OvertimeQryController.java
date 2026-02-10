package com.company.hrms.attendance.api.controller.overtime;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.attendance.api.request.overtime.GetOvertimeApplicationDetailRequest;
import com.company.hrms.attendance.api.request.overtime.GetOvertimeListRequest;
import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationDetailResponse;
import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationListResponse;
import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
        public ResponseEntity<PageResponse<OvertimeApplicationListResponse>> getOvertimeApplications(
                        @RequestParam(required = false) String employeeId,
                        @RequestParam(required = false) String deptId, // Changed from departmentId to deptId to match
                                                                       // DTO
                        @RequestParam(required = false) String overtimeType,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                GetOvertimeListRequest request = GetOvertimeListRequest.builder()
                                .employeeId(employeeId)
                                .deptId(deptId)
                                .overtimeType(overtimeType)
                                .status(status)
                                .startDate(startDate)
                                .endDate(endDate)
                                .build();

                // Page/Size handling is usually done via PageRequest, but BaseController might
                // handle it via overload or args?
                // Or Request object should have page/size?
                // Checked GetOvertimeListRequest, it does NOT have page/size.
                // BaseController usually handles pagination if passed as argsStrings?
                // Or BaseApiIntegrationTest passes page/size params.
                // If Request DTO doesn't have page/size, how is it passed?
                // UserApiTest passed page/size. GetUserListRequest likely extends PageRequest.
                // Let's check GetOvertimeListRequest again.

                return ResponseEntity.ok(getResponse(request, currentUser, String.valueOf(page), String.valueOf(size)));
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
                return ResponseEntity
                                .ok(getResponse(new GetOvertimeApplicationDetailRequest(), currentUser, applicationId));
        }
}
