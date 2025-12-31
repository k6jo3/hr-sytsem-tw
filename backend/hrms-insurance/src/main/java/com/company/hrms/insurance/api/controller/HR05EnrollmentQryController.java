package com.company.hrms.insurance.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 加退保管理 Query Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/enrollments")
@Slf4j
@Tag(name = "HR05-Enrollment", description = "加退保管理")
public class HR05EnrollmentQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢加退保記錄列表", operationId = "getEnrollments")
    public ResponseEntity<List<EnrollmentDetailResponse>> getEnrollments(
            @RequestParam(required = false) String employeeId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        return ResponseEntity.ok(getResponse(employeeId, currentUser));
    }

    @GetMapping("/active")
    @Operation(summary = "查詢員工有效加保記錄", operationId = "getActiveEnrollments")
    public ResponseEntity<List<EnrollmentDetailResponse>> getActiveEnrollments(
            @RequestParam String employeeId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        return ResponseEntity.ok(getResponse(employeeId, currentUser));
    }
}
