package com.company.hrms.organization.api.controller.employee;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.model.PageResponse;
import com.company.hrms.organization.api.request.employee.GetEmployeeListRequest;
import com.company.hrms.organization.api.response.employee.CheckUniqueResponse;
import com.company.hrms.organization.api.response.employee.EmployeeDetailResponse;
import com.company.hrms.organization.api.response.employee.EmployeeListItemResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR02 - 員工管理 Query Controller
 * 負責員工資料的查詢操作
 *
 * <p>
 * 命名規範：HR{DD}{Screen}QryController
 * </p>
 * <p>
 * DD = 02 (Organization Domain)
 * </p>
 */
@RestController
@RequestMapping("/api/v1/employees")
@Tag(name = "HR02-Employee-Query", description = "員工管理查詢操作")
public class HR02EmployeeQryController extends QueryBaseController {

        /**
         * 查詢員工列表
         */
        @Operation(summary = "查詢員工列表", operationId = "getEmployeeList")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping
        public ResponseEntity<PageResponse<EmployeeListItemResponse>> getEmployeeList(
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String departmentId,
                        @RequestParam(required = false) String hireDateFrom,
                        @RequestParam(required = false) String hireDateTo,
                        @RequestParam(defaultValue = "1") Integer page,
                        @RequestParam(defaultValue = "20") Integer size,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                GetEmployeeListRequest request = GetEmployeeListRequest
                                .builder()
                                .keyword(search)
                                .statuses(status != null ? java.util.List.of(status) : null)
                                .departmentIds(departmentId != null ? java.util.List.of(departmentId) : null)
                                .hireStartDate(hireDateFrom != null ? java.time.LocalDate.parse(hireDateFrom) : null)
                                .hireEndDate(hireDateTo != null ? java.time.LocalDate.parse(hireDateTo) : null)
                                .page(page)
                                .size(size)
                                .build();

                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        /**
         * 查詢員工詳細資料
         */
        @Operation(summary = "查詢員工詳細資料", operationId = "getEmployeeDetail")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "404", description = "員工不存在")
        })
        @GetMapping("/{employeeId}")
        public ResponseEntity<EmployeeDetailResponse> getEmployeeDetail(
                        @PathVariable String employeeId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(null, currentUser, employeeId));
        }

        /**
         * 查詢員工人事歷程
         */
        @Operation(summary = "查詢員工人事歷程", operationId = "getEmployeeHistory")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "404", description = "員工不存在")
        })
        @GetMapping("/{employeeId}/history")
        public ResponseEntity<Object> getEmployeeHistory(
                        @PathVariable String employeeId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(null, currentUser, employeeId));
        }

        /**
         * 查詢員工學歷
         */
        @Operation(summary = "查詢員工學歷", operationId = "getEmployeeEducations")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "404", description = "員工不存在")
        })
        @GetMapping("/{employeeId}/educations")
        public ResponseEntity<Object> getEmployeeEducations(
                        @PathVariable String employeeId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(null, currentUser, employeeId));
        }

        /**
         * 查詢員工工作經歷
         */
        @Operation(summary = "查詢員工工作經歷", operationId = "getEmployeeExperiences")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "404", description = "員工不存在")
        })
        @GetMapping("/{employeeId}/experiences")
        public ResponseEntity<Object> getEmployeeExperiences(
                        @PathVariable String employeeId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(null, currentUser, employeeId));
        }

        /**
         * 檢查員工編號唯一性
         */
        @Operation(summary = "檢查員工編號唯一性", operationId = "checkEmployeeNumber")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/check-number")
        public ResponseEntity<CheckUniqueResponse> checkEmployeeNumber(
                        @RequestParam String number,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(number, currentUser));
        }

        /**
         * 檢查身分證號唯一性
         */
        @Operation(summary = "檢查身分證號唯一性", operationId = "checkNationalId")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/check-national-id")
        public ResponseEntity<CheckUniqueResponse> checkNationalId(
                        @RequestParam String id,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(id, currentUser));
        }

        /**
         * 檢查Email唯一性
         */
        @Operation(summary = "檢查Email唯一性", operationId = "checkEmail")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/check-email")
        public ResponseEntity<CheckUniqueResponse> checkEmail(
                        @RequestParam String email,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(email, currentUser));
        }

        /**
         * 匯出員工資料
         */
        @Operation(summary = "匯出員工資料", operationId = "exportEmployees")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping("/export")
        public ResponseEntity<byte[]> exportEmployees(
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                byte[] content = getResponse(null, currentUser);
                return ResponseEntity.ok()
                                .header("Content-Disposition", "attachment; filename=\"employees.csv\"")
                                .header("Content-Type", "text/csv; charset=UTF-8")
                                .body(content);
        }
}
