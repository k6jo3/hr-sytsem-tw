package com.company.hrms.insurance.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.insurance.api.response.MyInsuranceResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;
import com.company.hrms.insurance.domain.service.InsuranceFeeCalculationService;
import com.company.hrms.insurance.domain.service.InsuranceLevelMatchingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * HR05 我的保險資訊 Query Controller (ESS)
 */
@RestController
@RequestMapping("/api/v1/insurance/my")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR05-MyInsurance", description = "我的保險資訊 (ESS)")
public class HR05MyInsuranceQryController {

    private final IInsuranceEnrollmentRepository enrollmentRepository;
    private final InsuranceFeeCalculationService feeCalculationService;
    private final InsuranceLevelMatchingService levelMatchingService;

    @GetMapping
    @Operation(summary = "查詢我的保險資訊", operationId = "getMyInsurance")
    public ResponseEntity<MyInsuranceResponse> getMyInsurance(
            @RequestParam String employeeId) {

        log.info("查詢我的保險資訊: employeeId={}", employeeId);

        // 查詢有效加保記錄
        List<InsuranceEnrollment> enrollments = enrollmentRepository.findAllActiveByEmployeeId(employeeId);

        if (enrollments.isEmpty()) {
            return ResponseEntity.ok(MyInsuranceResponse.builder()
                    .status("未加保")
                    .build());
        }

        // 取得勞保記錄計算費用
        InsuranceEnrollment laborEnrollment = enrollments.stream()
                .filter(e -> e.getInsuranceType() == InsuranceType.LABOR)
                .findFirst()
                .orElse(enrollments.get(0));

        // 查詢投保級距並計算保費
        var levelOpt = levelMatchingService.findAppropriateLevel(
                laborEnrollment.getMonthlySalary(),
                InsuranceType.LABOR,
                java.time.LocalDate.now());

        InsuranceFees fees = null;
        Integer levelNumber = null;
        if (levelOpt.isPresent()) {
            fees = feeCalculationService.calculate(levelOpt.get());
            levelNumber = levelOpt.get().getLevelNumber();
        }

        // 建構回應
        List<MyInsuranceResponse.EnrollmentSummary> summaries = enrollments.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        MyInsuranceResponse response = MyInsuranceResponse.builder()
                .enrollDate(laborEnrollment.getEnrollDate().toString())
                .monthlySalary(laborEnrollment.getMonthlySalary())
                .levelNumber(levelNumber)
                .status("正常投保中")
                .fees(fees != null ? toFeeDetail(fees) : null)
                .enrollments(summaries)
                .build();

        log.info("我的保險資訊查詢完成: employeeId={}", employeeId);

        return ResponseEntity.ok(response);
    }

    private MyInsuranceResponse.EnrollmentSummary toSummary(InsuranceEnrollment enrollment) {
        return MyInsuranceResponse.EnrollmentSummary.builder()
                .insuranceType(enrollment.getInsuranceType().getDisplayName())
                .status(enrollment.getStatus().getDisplayName())
                .enrollDate(enrollment.getEnrollDate().toString())
                .monthlySalary(enrollment.getMonthlySalary())
                .build();
    }

    private MyInsuranceResponse.FeeDetail toFeeDetail(InsuranceFees fees) {
        return MyInsuranceResponse.FeeDetail.builder()
                .laborEmployeeFee(fees.getLaborEmployeeFee())
                .laborEmployerFee(fees.getLaborEmployerFee())
                .healthEmployeeFee(fees.getHealthEmployeeFee())
                .healthEmployerFee(fees.getHealthEmployerFee())
                .pensionEmployerFee(fees.getPensionEmployerFee())
                .totalEmployeeFee(fees.getTotalEmployeeFee())
                .totalEmployerFee(fees.getTotalEmployerFee())
                .build();
    }
}
