package com.company.hrms.organization.application.service.department.task;

import java.util.UUID;

import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.department.context.DepartmentContext;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 指派主管 Task
 */
@Component
@RequiredArgsConstructor
public class AssignManagerTask implements PipelineTask<DepartmentContext> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(DepartmentContext context) throws Exception {
        var request = context.getAssignManagerRequest();
        var department = context.getDepartment();

        // 驗證主管存在
        EmployeeId managerId = new EmployeeId(request.getManagerId());
        var manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("MANAGER_NOT_FOUND",
                        "主管不存在: " + request.getManagerId()));

        // 記錄舊主管 ID (用於事件發布)
        if (department.getManagerId() != null) {
            context.setAttribute("oldManagerId", department.getManagerId().getValue().toString());
        }

        // 指派主管
        department.assignManager(UUID.fromString(request.getManagerId()));

        context.setManager(manager);
        context.setManagerName(manager.getLastName() + manager.getFirstName());
    }

    @Override
    public String getName() {
        return "指派主管";
    }
}
