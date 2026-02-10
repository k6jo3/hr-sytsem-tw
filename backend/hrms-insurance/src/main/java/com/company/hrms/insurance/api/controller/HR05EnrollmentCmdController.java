package com.company.hrms.insurance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.api.request.AdjustLevelRequest;
import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;
import com.company.hrms.insurance.api.request.WithdrawEnrollmentRequest;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 加退保管理 Command Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/enrollments")
@Slf4j
@Tag(name = "HR05-Enrollment", description = "加退保管理")
public class HR05EnrollmentCmdController extends CommandBaseController {

        @PostMapping
        @Operation(summary = "員工加保", operationId = "enrollEmployee")
        public ResponseEntity<EnrollmentDetailResponse> enrollEmployee(
                        @Valid @RequestBody EnrollEmployeeRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser));
        }

        @PutMapping("/{id}/withdraw")
        @Operation(summary = "退保", operationId = "withdrawEnrollment")
        public ResponseEntity<EnrollmentDetailResponse> withdrawEnrollment(
                        @PathVariable String id,
                        @Valid @RequestBody WithdrawEnrollmentRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser, id));
        }

        @PutMapping("/{id}/adjust-level")
        @Operation(summary = "調整投保級距", operationId = "adjustLevel")
        public ResponseEntity<EnrollmentDetailResponse> adjustLevel(
                        @PathVariable String id,
                        @Valid @RequestBody AdjustLevelRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser, id));
        }
}
