package com.company.hrms.organization.application.service.employee.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 儲存員工 Task (Infrastructure Task)
 * 將更新後的 Employee 聚合根持久化
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SaveEmployeeTask implements PipelineTask<EmployeeContext> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();

        employeeRepository.save(employee);

        log.debug("員工儲存成功: {}", employee.getId().getValue());
    }

    @Override
    public String getName() {
        return "儲存員工";
    }
}
