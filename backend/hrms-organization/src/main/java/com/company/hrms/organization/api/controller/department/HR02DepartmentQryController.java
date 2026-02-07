package com.company.hrms.organization.api.controller.department;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.model.PageResponse;
import com.company.hrms.organization.api.request.department.GetDepartmentListRequest;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;
import com.company.hrms.organization.api.response.department.DepartmentListItemResponse;
import com.company.hrms.organization.api.response.department.DepartmentManagersResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 部門管理查詢控制器
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR02-Department-Query", description = "部門管理查詢操作")
public class HR02DepartmentQryController extends QueryBaseController {

        @GetMapping
        @Operation(summary = "查詢部門清單", operationId = "getDepartmentList", description = "查詢部門列表,支援多種過濾條件。注意:為了性能考量,列表中的 employeeCount 固定為 0,如需員工數量請使用部門詳情 API。")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功")
        })
        public ResponseEntity<PageResponse<DepartmentListItemResponse>> getDepartmentList(
                        GetDepartmentListRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Getting department list: {}", request);
                return ResponseEntity.ok(getResponse(request, currentUser));
        }

        @GetMapping("/{departmentId}")
        @Operation(summary = "查詢部門詳情", operationId = "getDepartmentDetail")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "404", description = "部門不存在")
        })
        public ResponseEntity<DepartmentDetailResponse> getDepartmentDetail(
                        @PathVariable String departmentId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Getting department detail: {}", departmentId);
                return ResponseEntity.ok(getResponse(null, currentUser, departmentId));
        }

        @GetMapping("/{departmentId}/sub-departments")
        @Operation(summary = "查詢子部門列表", operationId = "getSubDepartments")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "404", description = "部門不存在")
        })
        public ResponseEntity<PageResponse<DepartmentListItemResponse>> getSubDepartments(
                        @PathVariable String departmentId,
                        GetDepartmentListRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Getting sub-departments for departmentId: {}", departmentId);
                return ResponseEntity.ok(getResponse(request, currentUser, departmentId));
        }

        @GetMapping("/{departmentId}/managers")
        @Operation(summary = "查詢部門主管層級", operationId = "getDepartmentManagers")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "404", description = "部門不存在")
        })
        public ResponseEntity<DepartmentManagersResponse> getDepartmentManagers(
                        @PathVariable String departmentId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Getting department managers: {}", departmentId);
                return ResponseEntity.ok(getResponse(null, currentUser, departmentId));
        }
}
