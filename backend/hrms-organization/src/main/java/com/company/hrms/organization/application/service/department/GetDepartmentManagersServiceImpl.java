package com.company.hrms.organization.application.service.department;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.department.DepartmentManagersResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 取得部門主管層級服務實作
 */
@Service("getDepartmentManagersServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetDepartmentManagersServiceImpl
        implements QueryApiService<Void, DepartmentManagersResponse> {

    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public DepartmentManagersResponse getResponse(Void request,
                                                  JWTModel currentUser,
                                                  String... args) throws Exception {
        String departmentId = args[0];
        log.info("Getting department managers: {}", departmentId);

        Department department = departmentRepository.findById(new DepartmentId(departmentId))
                .orElseThrow(() -> new IllegalArgumentException("部門不存在: " + departmentId));

        List<DepartmentManagersResponse.ManagerInfo> managers = new ArrayList<>();
        int managerLevel = 1;

        // 從當前部門往上查找主管
        Department currentDept = department;
        while (currentDept != null) {
            if (currentDept.getManagerId() != null) {
                Employee manager = employeeRepository.findById(currentDept.getManagerId())
                        .orElse(null);

                if (manager != null) {
                    managers.add(DepartmentManagersResponse.ManagerInfo.builder()
                            .employeeId(manager.getId().getValue())
                            .employeeNumber(manager.getEmployeeNumber())
                            .fullName(manager.getFullName())
                            .jobTitle(manager.getJobTitle())
                            .departmentId(currentDept.getId().getValue())
                            .departmentName(currentDept.getName())
                            .managerLevel(managerLevel++)
                            .build());
                }
            }

            // 移動到父部門
            if (currentDept.getParentId() != null) {
                currentDept = departmentRepository.findById(currentDept.getParentId())
                        .orElse(null);
            } else {
                currentDept = null;
            }
        }

        return DepartmentManagersResponse.builder()
                .departmentId(departmentId)
                .departmentName(department.getName())
                .managers(managers)
                .build();
    }
}
