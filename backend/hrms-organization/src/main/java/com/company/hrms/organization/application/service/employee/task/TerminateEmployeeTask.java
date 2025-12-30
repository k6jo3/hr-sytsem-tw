package com.company.hrms.organization.application.service.employee.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行離職 Task (Domain Task)
 * 呼叫 Employee 聚合根的 terminate 方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TerminateEmployeeTask implements PipelineTask<EmployeeContext> {

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();
        TerminateEmployeeRequest request = context.getTerminateRequest();

        // 呼叫聚合根業務方法
        employee.terminate(request.getTerminationDate(), request.getReason());

        log.info("離職執行: employeeId={}, date={}",
                context.getEmployeeId(),
                request.getTerminationDate());
    }

    @Override
    public String getName() {
        return "執行離職";
    }
}
