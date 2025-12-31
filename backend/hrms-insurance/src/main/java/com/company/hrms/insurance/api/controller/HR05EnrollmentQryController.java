package com.company.hrms.insurance.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 加退保管理 Query Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/enrollments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR05-Enrollment", description = "加退保管理")
public class HR05EnrollmentQryController {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @GetMapping
    @Operation(summary = "查詢加退保記錄列表", operationId = "getEnrollments")
    public ResponseEntity<List<EnrollmentDetailResponse>> getEnrollments(
            @RequestParam(required = false) String employeeId) {

        log.debug("查詢加退保記錄: employeeId={}", employeeId);

        List<InsuranceEnrollment> enrollments;
        if (employeeId != null && !employeeId.isBlank()) {
            enrollments = enrollmentRepository.findByEmployeeId(employeeId);
        } else {
            // 暫時返回空列表 (實際應透過分頁查詢)
            enrollments = List.of();
        }

        List<EnrollmentDetailResponse> response = enrollments.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "查詢員工有效加保記錄", operationId = "getActiveEnrollments")
    public ResponseEntity<List<EnrollmentDetailResponse>> getActiveEnrollments(
            @RequestParam String employeeId) {

        log.debug("查詢有效加保記錄: employeeId={}", employeeId);

        List<InsuranceEnrollment> enrollments = enrollmentRepository.findAllActiveByEmployeeId(employeeId);

        List<EnrollmentDetailResponse> response = enrollments.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private EnrollmentDetailResponse toDetailResponse(InsuranceEnrollment enrollment) {
        return EnrollmentDetailResponse.builder()
                .enrollmentId(enrollment.getId().getValue())
                .employeeId(enrollment.getEmployeeId())
                .insuranceType(enrollment.getInsuranceType().name())
                .insuranceTypeDisplay(enrollment.getInsuranceType().getDisplayName())
                .status(enrollment.getStatus().name())
                .statusDisplay(enrollment.getStatus().getDisplayName())
                .enrollDate(enrollment.getEnrollDate().toString())
                .withdrawDate(enrollment.getWithdrawDate() != null
                        ? enrollment.getWithdrawDate().toString()
                        : null)
                .monthlySalary(enrollment.getMonthlySalary())
                .build();
    }
}
