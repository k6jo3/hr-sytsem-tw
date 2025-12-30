package com.company.hrms.organization.application.service.employee.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行試用期轉正 Task (Domain Task)
 * 呼叫 Employee 聚合根的 completeProbation 方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CompleteProbationTask implements PipelineTask<EmployeeContext> {

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();

        // 呼叫聚合根業務方法
        employee.completeProbation();

        log.info("試用期轉正執行: employeeId={}", context.getEmployeeId());
    }

    @Override
    public String getName() {
        return "執行試用期轉正";
    }
}
