package com.company.hrms.insurance.application.service.enrollment.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.common.exception.ResourceAlreadyExistsException;
import com.company.hrms.insurance.application.service.enrollment.context.EnrollmentContext;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 驗證加保 Task
 * 檢查員工是否已有有效的加保記錄
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ValidateEnrollmentTask implements PipelineTask<EnrollmentContext> {

    private final IInsuranceEnrollmentRepository enrollmentRepository;

    @Override
    public void execute(EnrollmentContext context) throws Exception {
        String employeeId = context.getRequest().getEmployeeId();

        log.debug("驗證員工加保條件: employeeId={}", employeeId);

        // 檢查是否已有任何有效的加保記錄
        var activeEnrollments = enrollmentRepository.findAllActiveByEmployeeId(employeeId);
        if (!activeEnrollments.isEmpty()) {
            log.warn("員工已有有效的加保記錄: employeeId={}", employeeId);
            throw new ResourceAlreadyExistsException("ENROLLMENT_ALREADY_EXISTS", "員工已有有效的加保記錄，不可重複加保");
        }

        log.info("加保條件驗證通過: employeeId={}", employeeId);
    }

    @Override
    public String getName() {
        return "驗證加保條件";
    }

    @Override
    public boolean shouldExecute(EnrollmentContext context) {
        return context.getRequest() != null && context.getRequest().getEmployeeId() != null;
    }
}
