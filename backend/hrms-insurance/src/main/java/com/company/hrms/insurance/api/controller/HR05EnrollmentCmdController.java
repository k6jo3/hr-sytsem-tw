package com.company.hrms.insurance.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.insurance.api.request.AdjustLevelRequest;
import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;
import com.company.hrms.insurance.api.request.WithdrawEnrollmentRequest;
import com.company.hrms.insurance.api.response.EnrollEmployeeResponse;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.application.service.adjustment.context.AdjustmentContext;
import com.company.hrms.insurance.application.service.adjustment.task.FindNewLevelTask;
import com.company.hrms.insurance.application.service.adjustment.task.LoadEnrollmentForAdjustmentTask;
import com.company.hrms.insurance.application.service.adjustment.task.PerformAdjustmentTask;
import com.company.hrms.insurance.application.service.adjustment.task.SaveAdjustmentTask;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.application.service.enrollment.task.CalculateEnrollmentFeesTask;
import com.company.hrms.insurance.application.service.enrollment.task.CreateEnrollmentRecordsTask;
import com.company.hrms.insurance.application.service.enrollment.task.FindInsuranceLevelTask;
import com.company.hrms.insurance.application.service.enrollment.task.LoadInsuranceUnitTask;
import com.company.hrms.insurance.application.service.enrollment.task.SaveEnrollmentTask;
import com.company.hrms.insurance.application.service.withdrawal.context.WithdrawalContext;
import com.company.hrms.insurance.application.service.withdrawal.task.LoadEnrollmentTask;
import com.company.hrms.insurance.application.service.withdrawal.task.PerformWithdrawalTask;
import com.company.hrms.insurance.application.service.withdrawal.task.SaveWithdrawalTask;
import com.company.hrms.insurance.application.service.withdrawal.task.ValidateWithdrawalTask;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 加退保管理 Command Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/enrollments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR05-Enrollment", description = "加退保管理")
public class HR05EnrollmentCmdController {

        // 加保 Tasks
        private final LoadInsuranceUnitTask loadInsuranceUnitTask;
        private final FindInsuranceLevelTask findInsuranceLevelTask;
        private final CreateEnrollmentRecordsTask createEnrollmentRecordsTask;
        private final SaveEnrollmentTask saveEnrollmentTask;
        private final CalculateEnrollmentFeesTask calculateEnrollmentFeesTask;

        // 退保 Tasks
        private final LoadEnrollmentTask loadEnrollmentTask;
        private final ValidateWithdrawalTask validateWithdrawalTask;
        private final PerformWithdrawalTask performWithdrawalTask;
        private final SaveWithdrawalTask saveWithdrawalTask;

        // 調整級距 Tasks
        private final LoadEnrollmentForAdjustmentTask loadEnrollmentForAdjustmentTask;
        private final FindNewLevelTask findNewLevelTask;
        private final PerformAdjustmentTask performAdjustmentTask;
        private final SaveAdjustmentTask saveAdjustmentTask;

        @PostMapping
        @Operation(summary = "員工加保", operationId = "enrollEmployee")
        public ResponseEntity<EnrollEmployeeResponse> enrollEmployee(
                        @RequestBody EnrollEmployeeRequest request) throws Exception {

                log.info("員工加保請求: employeeId={}", request.getEmployeeId());

                // 建立 Context
                EnrollmentContext context = new EnrollmentContext(request, "default");

                // 執行 Pipeline (使用 fluent API)
                BusinessPipeline.start(context)
                                .next(loadInsuranceUnitTask)
                                .next(findInsuranceLevelTask)
                                .next(createEnrollmentRecordsTask)
                                .next(saveEnrollmentTask)
                                .next(calculateEnrollmentFeesTask)
                                .execute();

                // 建構回應
                EnrollEmployeeResponse response = buildEnrollResponse(context);

                log.info("員工加保成功: employeeId={}, 共 {} 筆",
                                request.getEmployeeId(), context.getEnrollments().size());

                return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}/withdraw")
        @Operation(summary = "退保", operationId = "withdrawEnrollment")
        public ResponseEntity<EnrollmentDetailResponse> withdrawEnrollment(
                        @PathVariable String id,
                        @RequestBody WithdrawEnrollmentRequest request) throws Exception {

                log.info("退保請求: enrollmentId={}, date={}", id, request.getWithdrawDate());

                // 建立 Context
                WithdrawalContext context = new WithdrawalContext(id, request, "default");

                // 執行 Pipeline
                BusinessPipeline.start(context)
                                .next(loadEnrollmentTask)
                                .next(validateWithdrawalTask)
                                .next(performWithdrawalTask)
                                .next(saveWithdrawalTask)
                                .execute();

                // 建構回應
                EnrollmentDetailResponse response = toDetailResponse(context.getEnrollment());

                log.info("退保成功: enrollmentId={}", id);

                return ResponseEntity.ok(response);
        }

        @PutMapping("/{id}/adjust-level")
        @Operation(summary = "調整投保級距", operationId = "adjustLevel")
        public ResponseEntity<EnrollmentDetailResponse> adjustLevel(
                        @PathVariable String id,
                        @RequestBody AdjustLevelRequest request) throws Exception {

                log.info("調整級距請求: enrollmentId={}, newSalary={}", id, request.getNewMonthlySalary());

                // 建立 Context
                AdjustmentContext context = new AdjustmentContext(id, request, "default");

                // 執行 Pipeline
                BusinessPipeline.start(context)
                                .next(loadEnrollmentForAdjustmentTask)
                                .next(findNewLevelTask)
                                .next(performAdjustmentTask)
                                .next(saveAdjustmentTask)
                                .execute();

                // 建構回應
                EnrollmentDetailResponse response = toDetailResponse(context.getEnrollment());

                log.info("調整級距成功: enrollmentId={}", id);

                return ResponseEntity.ok(response);
        }

        // ==================== Private Methods ====================

        private EnrollEmployeeResponse buildEnrollResponse(EnrollmentContext context) {
                List<EnrollEmployeeResponse.EnrollmentRecord> records = context.getEnrollments().stream()
                                .map(this::toEnrollmentRecord)
                                .collect(Collectors.toList());

                return EnrollEmployeeResponse.builder()
                                .employeeId(context.getRequest().getEmployeeId())
                                .enrollments(records)
                                .totalEmployeeFee(context.getFees().getTotalEmployeeFee())
                                .totalEmployerFee(context.getFees().getTotalEmployerFee())
                                .build();
        }

        private EnrollEmployeeResponse.EnrollmentRecord toEnrollmentRecord(InsuranceEnrollment enrollment) {
                return EnrollEmployeeResponse.EnrollmentRecord.builder()
                                .enrollmentId(enrollment.getId().getValue())
                                .insuranceType(enrollment.getInsuranceType().getDisplayName())
                                .enrollDate(enrollment.getEnrollDate().toString())
                                .monthlySalary(enrollment.getMonthlySalary())
                                .build();
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
