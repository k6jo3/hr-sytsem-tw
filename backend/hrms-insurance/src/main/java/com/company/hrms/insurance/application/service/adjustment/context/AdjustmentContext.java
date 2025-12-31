package com.company.hrms.insurance.application.service.adjustment.context;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.insurance.api.request.AdjustLevelRequest;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;

import lombok.Getter;
import lombok.Setter;

/**
 * 調整投保級距 Pipeline Context
 */
@Getter
@Setter
public class AdjustmentContext extends PipelineContext {

    // 輸入
    private final EnrollmentId enrollmentId;
    private final AdjustLevelRequest request;
    private final String tenantId;

    // 中間數據
    private InsuranceEnrollment enrollment;
    private InsuranceLevel newLevel;
    private BigDecimal newMonthlySalary;
    private LocalDate effectiveDate;

    public AdjustmentContext(String enrollmentId, AdjustLevelRequest request, String tenantId) {
        this.enrollmentId = new EnrollmentId(enrollmentId);
        this.request = request;
        this.tenantId = tenantId;
        this.newMonthlySalary = request.getNewMonthlySalary();
        this.effectiveDate = request.getEffectiveDate() != null
                ? LocalDate.parse(request.getEffectiveDate())
                : LocalDate.now();
    }
}
