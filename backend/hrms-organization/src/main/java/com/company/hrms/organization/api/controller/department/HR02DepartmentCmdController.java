package com.company.hrms.organization.api.controller.department;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.organization.api.request.department.AssignManagerRequest;
import com.company.hrms.organization.api.request.department.CreateDepartmentRequest;
import com.company.hrms.organization.api.request.department.ReorderDepartmentRequest;
import com.company.hrms.organization.api.request.department.UpdateDepartmentRequest;
import com.company.hrms.organization.api.response.department.CreateDepartmentResponse;
import com.company.hrms.organization.api.response.department.DepartmentDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 部門管理寫入控制器
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR02-Department-Command", description = "部門管理寫入操作")
public class HR02DepartmentCmdController extends CommandBaseController {

        @PostMapping
        @Operation(summary = "新增部門", operationId = "createDepartment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "請求格式錯誤或超過最大層級"),
                        @ApiResponse(responseCode = "409", description = "部門代碼已存在")
        })
        public ResponseEntity<CreateDepartmentResponse> createDepartment(
                        @Valid @RequestBody CreateDepartmentRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Creating department: {}", request.getCode());
                return ResponseEntity.ok(execCommand(request, currentUser));
        }

        @PutMapping("/{departmentId}")
        @Operation(summary = "更新部門", operationId = "updateDepartment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
                        @ApiResponse(responseCode = "404", description = "部門不存在")
        })
        public ResponseEntity<DepartmentDetailResponse> updateDepartment(
                        @PathVariable String departmentId,
                        @Valid @RequestBody UpdateDepartmentRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Updating department: {}", departmentId);
                return ResponseEntity.ok(execCommand(request, currentUser, departmentId));
        }

        @DeleteMapping("/{departmentId}")
        @Operation(summary = "刪除部門", operationId = "deleteDepartment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "部門下有在職員工或子部門，無法刪除"),
                        @ApiResponse(responseCode = "404", description = "部門不存在")
        })
        public ResponseEntity<Void> deleteDepartment(
                        @PathVariable String departmentId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Deleting department: {}", departmentId);
                execCommand(null, currentUser, departmentId);
                return ResponseEntity.noContent().build();
        }

        @PutMapping("/{departmentId}/assign-manager")
        @Operation(summary = "指派部門主管", operationId = "assignManager")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
                        @ApiResponse(responseCode = "404", description = "部門或員工不存在")
        })
        public ResponseEntity<DepartmentDetailResponse> assignManager(
                        @PathVariable String departmentId,
                        @Valid @RequestBody AssignManagerRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Assigning manager {} to department: {}", request.getManagerId(), departmentId);
                return ResponseEntity.ok(execCommand(request, currentUser, departmentId));
        }

        @PutMapping("/{departmentId}/reorder")
        @Operation(summary = "調整部門順序", operationId = "reorderDepartment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "404", description = "部門不存在")
        })
        public ResponseEntity<DepartmentDetailResponse> reorderDepartment(
                        @PathVariable String departmentId,
                        @Valid @RequestBody ReorderDepartmentRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Reordering department: {} to {}", departmentId, request.getSortOrder());
                return ResponseEntity.ok(execCommand(request, currentUser, departmentId));
        }

        @PutMapping("/{departmentId}/deactivate")
        @Operation(summary = "停用部門", operationId = "deactivateDepartment")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "400", description = "部門下有在職員工或子部門，無法停用"),
                        @ApiResponse(responseCode = "404", description = "部門不存在")
        })
        public ResponseEntity<DepartmentDetailResponse> deactivateDepartment(
                        @PathVariable String departmentId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                log.info("Deactivating department: {}", departmentId);
                return ResponseEntity.ok(execCommand(null, currentUser, departmentId));
        }
}
