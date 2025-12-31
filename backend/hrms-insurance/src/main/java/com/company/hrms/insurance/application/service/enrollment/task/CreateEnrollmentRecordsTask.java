package com.company.hrms.insurance.application.service.enrollment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;

import lombok.extern.slf4j.Slf4j;

/**
 * 建立加保記錄 Task
 * 建立勞保、健保、勞退三筆加保記錄
 */
@Component
@Slf4j
public class CreateEnrollmentRecordsTask implements PipelineTask<EnrollmentContext> {

    @Override
    public void execute(EnrollmentContext context) throws Exception {
        var request = context.getRequest();
        var unit = context.getInsuranceUnit();
        var enrollDate = context.getEnrollDate();

        log.debug("建立加保記錄: employeeId={}, date={}", request.getEmployeeId(), enrollDate);

        // 勞保加保
        InsuranceEnrollment laborEnrollment = InsuranceEnrollment.enroll(
                request.getEmployeeId(),
                unit.getId(),
                InsuranceType.LABOR,
                context.getLaborLevel(),
                enrollDate);
        context.addEnrollment(laborEnrollment);

        // 健保加保
        InsuranceEnrollment healthEnrollment = InsuranceEnrollment.enroll(
                request.getEmployeeId(),
                unit.getId(),
                InsuranceType.HEALTH,
                context.getHealthLevel(),
                enrollDate);
        context.addEnrollment(healthEnrollment);

        // 勞退加保
        InsuranceEnrollment pensionEnrollment = InsuranceEnrollment.enroll(
                request.getEmployeeId(),
                unit.getId(),
                InsuranceType.PENSION,
                context.getPensionLevel(),
                enrollDate);
        context.addEnrollment(pensionEnrollment);

        log.info("加保記錄建立成功: 共 {} 筆", context.getEnrollments().size());
    }

    @Override
    public String getName() {
        return "建立加保記錄";
    }

    @Override
    public boolean shouldExecute(EnrollmentContext context) {
        return context.getInsuranceUnit() != null && context.getLaborLevel() != null;
    }
}
