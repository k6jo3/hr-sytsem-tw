package com.company.hrms.organization.application.service.employee.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.application.service.employee.context.EmployeeContext;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 載入部門 Task (Infrastructure Task)
 * 載入新舊部門資料供後續使用
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LoadDepartmentsTask implements PipelineTask<EmployeeContext> {

    private final IDepartmentRepository departmentRepository;

    @Override
    public void execute(EmployeeContext context) throws Exception {
        // 載入舊部門
        String oldDeptId = context.getOldDepartmentId();
        if (oldDeptId != null) {
            Department oldDept = departmentRepository.findById(new DepartmentId(oldDeptId))
                    .orElse(null);
            context.setOldDepartment(oldDept);
            log.debug("舊部門載入: {}", oldDept != null ? oldDept.getName() : "null");
        }

        // 載入新部門
        String newDeptId = context.getNewDepartmentId();
        if (newDeptId != null) {
            Department newDept = departmentRepository.findById(new DepartmentId(newDeptId))
                    .orElseThrow(() -> new IllegalArgumentException("部門不存在: " + newDeptId));
            context.setNewDepartment(newDept);
            log.debug("新部門載入: {}", newDept.getName());
        }
    }

    @Override
    public String getName() {
        return "載入部門資料";
    }

    @Override
    public boolean shouldExecute(EmployeeContext context) {
        // 只在有部門調動需求時執行
        return context.getTransferRequest() != null;
    }
}
