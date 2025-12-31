package com.company.hrms.insurance.application.service.enrollment.context;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.insurance.api.request.EnrollEmployeeRequest;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceUnit;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceFees;

import lombok.Getter;
import lombok.Setter;

/**
 * 加保 Pipeline Context
 */
@Getter
@Setter
public class EnrollmentContext extends PipelineContext {

    // 輸入
    private final EnrollEmployeeRequest request;
    private final String tenantId;

    // 中間數據
    private InsuranceUnit insuranceUnit;
    private InsuranceLevel laborLevel;
    private InsuranceLevel healthLevel;
    private InsuranceLevel pensionLevel;
    private LocalDate enrollDate;
    private BigDecimal monthlySalary;

    // 輸出
    private List<InsuranceEnrollment> enrollments = new ArrayList<>();
    private InsuranceFees fees;

    public EnrollmentContext(EnrollEmployeeRequest request, String tenantId) {
        this.request = request;
        this.tenantId = tenantId;
        this.monthlySalary = request.getMonthlySalary();
        this.enrollDate = LocalDate.parse(request.getEnrollDate());
    }

    public void addEnrollment(InsuranceEnrollment enrollment) {
        this.enrollments.add(enrollment);
    }
}
