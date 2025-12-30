package com.company.hrms.organization.application.service.employee.task;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.request.employee.TransferEmployeeRequest;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.event.EmployeeDepartmentChangedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 發布部門調動事件 Task (Integration Task)
 * 發布領域事件通知其他服務
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PublishDepartmentChangedEventTask implements PipelineTask<EmployeeContext> {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        Employee employee = context.getEmployee();
        TransferEmployeeRequest request = context.getTransferRequest();

        UUID employeeIdUuid = UUID.fromString(context.getEmployeeId());
        UUID oldDeptId = context.getOldDepartmentId() != null
                ? UUID.fromString(context.getOldDepartmentId())
                : null;
        UUID newDeptId = UUID.fromString(request.getNewDepartmentId());
        UUID newManagerId = request.getNewManagerId() != null
                ? UUID.fromString(request.getNewManagerId())
                : null;
        UUID oldManagerId = employee.getManagerId();

        EmployeeDepartmentChangedEvent event = new EmployeeDepartmentChangedEvent(
                employeeIdUuid,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                oldDeptId,
                newDeptId,
                oldManagerId,
                newManagerId,
                request.getEffectiveDate(),
                request.getReason());

        eventPublisher.publishEvent(event);

        String oldDeptName = context.getOldDepartment() != null
                ? context.getOldDepartment().getName()
                : "未知";
        String newDeptName = context.getNewDepartment() != null
                ? context.getNewDepartment().getName()
                : "未知";

        log.info("部門調動事件發布: {} - {} -> {}",
                employee.getFullName(),
                oldDeptName,
                newDeptName);
    }

    @Override
    public String getName() {
        return "發布部門調動事件";
    }
}
