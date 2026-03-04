package com.company.hrms.insurance.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.insurance.api.response.EnrollmentDetailResponse;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceUnit;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;
import com.company.hrms.insurance.domain.repository.IInsuranceUnitRepository;

import lombok.RequiredArgsConstructor;

/**
 * 加退保記錄回應組裝器
 * 統一補全 employeeName、insuranceUnitName、levelNumber 欄位
 */
@Component
@RequiredArgsConstructor
public class EnrollmentResponseAssembler {

    private final IInsuranceUnitRepository unitRepository;
    private final IInsuranceLevelRepository levelRepository;

    /**
     * 將 InsuranceEnrollment 轉換為 EnrollmentDetailResponse
     *
     * @param enrollment   加退保記錄
     * @param employeeName 員工姓名（跨服務無法取得時傳 null）
     */
    public EnrollmentDetailResponse toDetailResponse(InsuranceEnrollment enrollment, String employeeName) {
        // 查詢投保單位名稱
        String unitName = unitRepository.findById(enrollment.getInsuranceUnitId())
                .map(InsuranceUnit::getUnitName).orElse(null);

        // 查詢投保級距
        Integer levelNumber = null;
        if (enrollment.getInsuranceLevelId() != null) {
            levelNumber = levelRepository.findById(enrollment.getInsuranceLevelId())
                    .map(InsuranceLevel::getLevelNumber).orElse(null);
        }

        return EnrollmentDetailResponse.builder()
                .enrollmentId(enrollment.getId().getValue())
                .employeeId(enrollment.getEmployeeId())
                .employeeName(employeeName)
                .insuranceUnitName(unitName)
                .levelNumber(levelNumber)
                .insuranceType(enrollment.getInsuranceType().name())
                .insuranceTypeDisplay(enrollment.getInsuranceType().getDisplayName())
                .status(enrollment.getStatus().name())
                .statusDisplay(enrollment.getStatus().getDisplayName())
                .enrollDate(enrollment.getEnrollDate().toString())
                .withdrawDate(enrollment.getWithdrawDate() != null
                        ? enrollment.getWithdrawDate().toString() : null)
                .monthlySalary(enrollment.getMonthlySalary())
                .build();
    }
}
