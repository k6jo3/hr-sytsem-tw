package com.company.hrms.organization.application.service.employee.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.PromoteEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行升遷 Task (Domain Task)
 * 呼叫 Employee 聚合根的 promote 方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromoteEmployeeTask implements PipelineTask<EmployeeContext> {

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();
        PromoteEmployeeRequest request = context.getPromoteRequest();

        // 記錄舊值（用於事件和回應）
        context.setAttribute("oldJobTitle", employee.getJobTitle());
        context.setAttribute("oldJobLevel", employee.getJobLevel());

        // 呼叫聚合根業務方法
        employee.promote(request.getNewJobTitle(), request.getNewJobLevel());

        log.info("升遷執行: employeeId={}, {} -> {}",
                context.getEmployeeId(),
                context.getAttribute("oldJobTitle"),
                request.getNewJobTitle());
    }

    @Override
    public String getName() {
        return "執行升遷";
    }
}
