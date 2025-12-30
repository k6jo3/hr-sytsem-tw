package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.TransferEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 執行部門調動 Task (Domain Task)
 * 呼叫 Employee 聚合根的 transferDepartment 方法
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TransferDepartmentTask implements PipelineTask<EmployeeContext> {

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();
        TransferEmployeeRequest request = context.getTransferRequest();

        // 轉換 ID
        UUID newDepartmentId = UUID.fromString(request.getNewDepartmentId());
        UUID newManagerId = request.getNewManagerId() != null
                ? UUID.fromString(request.getNewManagerId())
                : null;

        // 呼叫聚合根業務方法
        employee.transferDepartment(newDepartmentId, newManagerId);

        log.info("部門調動執行: {} -> {}",
                context.getOldDepartmentId(),
                request.getNewDepartmentId());
    }

    @Override
    public String getName() {
        return "執行部門調動";
    }
}
