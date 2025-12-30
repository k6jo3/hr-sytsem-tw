package com.company.hrms.organization.application.service.organization.task;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.organization.api.response.organization.OrganizationTreeResponse;
import com.company.hrms.organization.application.service.organization.context.OrganizationContext;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;

/**
 * 載入部門樹 Task
 */
@Component
@RequiredArgsConstructor
public class LoadDepartmentTreeTask implements PipelineTask<OrganizationContext> {

    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public void execute(OrganizationContext context) throws Exception {
        var organization = context.getOrganization();

        // 查詢根部門
        List<Department> rootDepartments = departmentRepository.findRootDepartments(organization.getId());

        // 遞迴建立部門樹
        List<OrganizationTreeResponse.DepartmentTreeNode> nodes = new ArrayList<>();
        for (Department dept : rootDepartments) {
            nodes.add(buildDepartmentNode(dept, 1, 5));
        }

        context.setAttribute("departmentNodes", nodes);
    }

    private OrganizationTreeResponse.DepartmentTreeNode buildDepartmentNode(
            Department dept, int currentLevel, int maxLevel) {

        // 查詢子部門
        List<OrganizationTreeResponse.DepartmentTreeNode> children = new ArrayList<>();
        if (currentLevel < maxLevel) {
            List<Department> childDepts = departmentRepository.findByParentId(dept.getId());
            for (Department child : childDepts) {
                children.add(buildDepartmentNode(child, currentLevel + 1, maxLevel));
            }
        }

        // 取得主管名稱
        String managerName = null;
        if (dept.getManagerId() != null) {
            Employee manager = employeeRepository.findById(dept.getManagerId()).orElse(null);
            if (manager != null) {
                managerName = manager.getFirstName() + " " + manager.getLastName();
            }
        }

        return OrganizationTreeResponse.DepartmentTreeNode.builder()
                .departmentId(dept.getId().getValue().toString())
                .code(dept.getCode())
                .name(dept.getName())
                .level(dept.getLevel())
                .managerId(dept.getManagerId() != null ? dept.getManagerId().getValue().toString() : null)
                .managerName(managerName)
                .children(children)
                .build();
    }

    @Override
    public String getName() {
        return "載入部門樹";
    }
}
