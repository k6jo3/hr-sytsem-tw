package com.company.hrms.insurance.application.service.withdrawal.context;

import java.time.LocalDate;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.insurance.api.request.WithdrawEnrollmentRequest;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;

import lombok.Getter;
import lombok.Setter;

/**
 * 退保 Pipeline Context
 */
@Getter
@Setter
public class WithdrawalContext extends PipelineContext {

    // 輸入
    private final EnrollmentId enrollmentId;
    private final WithdrawEnrollmentRequest request;
    private final String tenantId;

    // 中間數據
    private InsuranceEnrollment enrollment;
    private LocalDate withdrawDate;

    public WithdrawalContext(String enrollmentId, WithdrawEnrollmentRequest request, String tenantId) {
        this.enrollmentId = new EnrollmentId(enrollmentId);
        this.request = request;
        this.tenantId = tenantId;
        this.withdrawDate = LocalDate.parse(request.getWithdrawDate());
    }
}
