package com.company.hrms.insurance.application.service.my;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.api.response.FeeCalculationResponse;
import com.company.hrms.insurance.api.response.MyInsuranceDetailResponse;
import com.company.hrms.insurance.api.response.MyInsuranceDetailResponse.EnrollmentHistoryItem;
import com.company.hrms.insurance.application.assembler.EnrollmentResponseAssembler;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;
import com.company.hrms.insurance.domain.service.InsuranceFeeCalculationService;
import com.company.hrms.insurance.domain.service.InsuranceLevelMatchingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 我的保險 (ESS) Service
 * 回傳加保記錄、保費計算、投保歷程
 */
@Service("getMyInsuranceServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class GetMyInsuranceServiceImpl implements QueryApiService<Void, MyInsuranceDetailResponse> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;
    private final IInsuranceLevelRepository levelRepository;
    private final EnrollmentResponseAssembler assembler;
    private final InsuranceLevelMatchingService levelMatchingService;
    private final InsuranceFeeCalculationService feeCalculationService;

    @Override
    public MyInsuranceDetailResponse getResponse(Void request, JWTModel currentUser, String... args)
            throws Exception {

        String employeeId = currentUser.getUserId();
        String employeeName = currentUser.getDisplayName();
        log.debug("查詢我的保險: employeeId={}, employeeName={}", employeeId, employeeName);

        // 1. 查詢所有加保記錄
        List<InsuranceEnrollment> enrollments = enrollmentRepository.findByEmployeeId(employeeId);
        List<EnrollmentDetailResponse> details = enrollments.stream()
                .map(e -> assembler.toDetailResponse(e, employeeName))
                .collect(Collectors.toList());

        // 2. 計算保費
        FeeCalculationResponse fees = calculateFees(enrollments);

        // 3. 組裝投保歷程
        List<EnrollmentHistoryItem> history = buildHistory(enrollments);

        // 4. 取投保單位名稱
        String unitName = details.isEmpty() ? null : details.get(0).getInsuranceUnitName();

        return MyInsuranceDetailResponse.builder()
                .employeeName(employeeName)
                .unitName(unitName)
                .enrollments(details)
                .fees(fees)
                .history(history)
                .build();
    }

    /**
     * 計算保費：取 ACTIVE 狀態的勞保級距計算
     */
    private FeeCalculationResponse calculateFees(List<InsuranceEnrollment> enrollments) {
        // 找到 ACTIVE 狀態的勞保加保記錄
        Optional<InsuranceEnrollment> activeLaborOpt = enrollments.stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE)
                .filter(e -> e.getInsuranceType() == InsuranceType.LABOR)
                .findFirst();

        if (activeLaborOpt.isEmpty()) {
            return emptyFees();
        }

        BigDecimal salary = activeLaborOpt.get().getMonthlySalary();

        // 使用 Domain Service 查找對應級距
        Optional<InsuranceLevel> levelOpt = levelMatchingService
                .findAppropriateLevel(salary, InsuranceType.LABOR, LocalDate.now());

        if (levelOpt.isEmpty()) {
            return emptyFees();
        }

        InsuranceLevel level = levelOpt.get();
        InsuranceFees fees = feeCalculationService.calculate(level);

        return FeeCalculationResponse.builder()
                .levelNumber(level.getLevelNumber())
                .monthlySalary(level.getMonthlySalary())
                .laborEmployeeFee(fees.getLaborEmployeeFee())
                .laborEmployerFee(fees.getLaborEmployerFee())
                .healthEmployeeFee(fees.getHealthEmployeeFee())
                .healthEmployerFee(fees.getHealthEmployerFee())
                .pensionEmployerFee(fees.getPensionEmployerFee())
                .pensionSelfContribution(BigDecimal.ZERO)
                .totalEmployeeFee(fees.getTotalEmployeeFee())
                .totalEmployerFee(fees.getTotalEmployerFee())
                .build();
    }

    /**
     * 從現有加保記錄推導投保歷程
     */
    private List<EnrollmentHistoryItem> buildHistory(List<InsuranceEnrollment> enrollments) {
        List<EnrollmentHistoryItem> history = new ArrayList<>();
        for (InsuranceEnrollment e : enrollments) {
            // 查詢級距編號
            Integer levelNumber = null;
            if (e.getInsuranceLevelId() != null) {
                levelNumber = levelRepository.findById(e.getInsuranceLevelId())
                        .map(InsuranceLevel::getLevelNumber).orElse(null);
            }

            // 加保事件
            history.add(EnrollmentHistoryItem.builder()
                    .historyId(e.getId().getValue() + "-enroll")
                    .changeDate(e.getEnrollDate().toString())
                    .changeType("ENROLL")
                    .insuranceType(e.getInsuranceType().name())
                    .monthlySalary(e.getMonthlySalary())
                    .levelNumber(levelNumber)
                    .reason("新進員工加保")
                    .build());

            // 退保事件
            if (e.getWithdrawDate() != null) {
                history.add(EnrollmentHistoryItem.builder()
                        .historyId(e.getId().getValue() + "-withdraw")
                        .changeDate(e.getWithdrawDate().toString())
                        .changeType("WITHDRAW")
                        .insuranceType(e.getInsuranceType().name())
                        .monthlySalary(e.getMonthlySalary())
                        .levelNumber(levelNumber)
                        .reason("員工退保")
                        .build());
            }
        }
        // 依日期降冪排序
        history.sort(Comparator.comparing(EnrollmentHistoryItem::getChangeDate).reversed());
        return history;
    }

    private FeeCalculationResponse emptyFees() {
        return FeeCalculationResponse.builder()
                .levelNumber(0)
                .monthlySalary(BigDecimal.ZERO)
                .laborEmployeeFee(BigDecimal.ZERO)
                .laborEmployerFee(BigDecimal.ZERO)
                .healthEmployeeFee(BigDecimal.ZERO)
                .healthEmployerFee(BigDecimal.ZERO)
                .pensionEmployerFee(BigDecimal.ZERO)
                .pensionSelfContribution(BigDecimal.ZERO)
                .totalEmployeeFee(BigDecimal.ZERO)
                .totalEmployerFee(BigDecimal.ZERO)
                .build();
    }
}
