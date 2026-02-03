package com.company.hrms.organization.application.service.department;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.department.DepartmentManagersResponse;
import com.company.hrms.organization.api.response.department.DepartmentManagersResponse.ManagerInfo;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢部門主管層級 Application Service
 */
@Service("getDepartmentManagersServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetDepartmentManagersServiceImpl implements QueryApiService<Object, DepartmentManagersResponse> {

    private final IDepartmentRepository departmentRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public DepartmentManagersResponse getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {

        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Department ID is required");
        }
        String departmentIdStr = args[0];
        DepartmentId departmentId = new DepartmentId(departmentIdStr);

        // 1. 查詢目標部門
        Department targetDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DomainException("DEPARTMENT_NOT_FOUND", "部門不存在: " + departmentIdStr));

        // 2. 優化查詢：一次查出該組織下所有部門，避免 N+1 查詢
        // 先查出該組織所有部門，建立 Map<DepartmentId, Department>
        Map<DepartmentId, Department> allDepartmentsMap = new HashMap<>();
        if (targetDepartment.getOrganizationId() != null) {
            allDepartmentsMap = departmentRepository.findByOrganizationId(targetDepartment.getOrganizationId())
                    .stream()
                    .collect(Collectors.toMap(Department::getId, Function.identity()));
        }

        // 向上遍歷部門層級，收集部門路徑
        List<Department> departmentPath = new ArrayList<>();
        Department current = targetDepartment;
        while (current != null) {
            departmentPath.add(current);
            if (current.getParentId() != null) {
                current = allDepartmentsMap.get(current.getParentId());
            } else {
                current = null;
            }
        }

        // 3. 收集所有主管 ID
        Set<EmployeeId> managerIds = departmentPath.stream()
                .map(Department::getManagerId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 4. 批量查詢主管資訊
        Map<EmployeeId, Employee> managerMap = new HashMap<>();
        if (!managerIds.isEmpty()) {
            managerMap = employeeRepository.findByIdIn(managerIds).stream()
                    .collect(Collectors.toMap(Employee::getId, Function.identity()));
        }

        // 5. 構建回應 (層級由近到遠: 直屬, 二級, 三級...)
        List<ManagerInfo> managers = new ArrayList<>();
        int level = 1;

        // 我們還需要每個主管"所屬"的部門名稱，這需要再查一次主管的 departmentId 對應的部門嗎？
        // 題目要求 ManagerInfo 包含 "所屬部門ID" 和 "所屬部門名稱"。
        // 注意：這裡的"所屬部門"是指該主管"擔任主管"的部門 (即 current iterate dept)，還是該主管"歸屬"的部門
        // (Employee.departmentId)?
        // 通常 "管理層級" 列表是用來審批流程的。
        // 上下文: "Query Department Managers List (including upper level managers)".
        // ManagerInfo 裡的 departmentId/Name 應該是指該主管 *歸屬* 的部門，還是他 *管理* 的部門？
        // 參考 ManagerInfo 的欄位說明: "所屬部門ID", "所屬部門名稱"。以及 "管理層級"。
        // 如果是該主管歸屬的部門，我們需要拿到 Employee 物件裡的 departmentId，然後再去查部門名稱。
        // 這會導致額外的部門查詢。

        // 讓我們優化一下：
        // 為了簡單起見，我們這裡對主管的歸屬部門做個別查詢 (Cache 起來)。
        // 考慮到 departmentPath 已經包含了一些部門，我們可以先用 departmentPath 建立此 Cache。
        Map<String, String> deptIdNameMap = departmentPath.stream()
                .collect(Collectors.toMap(d -> d.getId().getValue().toString(), Department::getName, (a, b) -> a));

        for (Department dept : departmentPath) {
            EmployeeId managerId = dept.getManagerId();
            if (managerId == null)
                continue;

            Employee manager = managerMap.get(managerId);
            if (manager == null)
                continue;

            String managersDeptId = manager.getDepartmentIdVO() != null
                    ? manager.getDepartmentIdVO().getValue().toString()
                    : "";
            String managersDeptName = "";

            if (!managersDeptId.isEmpty()) {
                if (deptIdNameMap.containsKey(managersDeptId)) {
                    managersDeptName = deptIdNameMap.get(managersDeptId);
                } else {
                    // 優先從 allDepartmentsMap 查詢
                    Department d = allDepartmentsMap.get(new DepartmentId(managersDeptId));
                    if (d == null) {
                        // 如果真的找不到，才去 DB 查 (這應該很少發生，除非是在跨組織查詢或者資料不一致)
                        d = departmentRepository.findById(new DepartmentId(managersDeptId)).orElse(null);
                    }

                    if (d != null) {
                        managersDeptName = d.getName();
                        deptIdNameMap.put(managersDeptId, managersDeptName);
                    }
                }
            }

            ManagerInfo info = ManagerInfo.builder()
                    .employeeId(manager.getId().getValue().toString())
                    .employeeNumber(manager.getEmployeeNumber())
                    .fullName(manager.getFullName())
                    .jobTitle(manager.getJobTitle())
                    .departmentId(managersDeptId)
                    .departmentName(managersDeptName)
                    .managerLevel(level++)
                    .build();

            managers.add(info);
        }

        return DepartmentManagersResponse.builder()
                .departmentId(targetDepartment.getId().getValue().toString())
                .departmentName(targetDepartment.getName())
                .managers(managers)
                .build();
    }
}
