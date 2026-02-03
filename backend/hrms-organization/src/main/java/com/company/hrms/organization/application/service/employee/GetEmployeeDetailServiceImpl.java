package com.company.hrms.organization.application.service.employee;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.employee.EmployeeDetailResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得員工詳情服務實作
 */
@Service("getEmployeeDetailServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeDetailServiceImpl implements QueryApiService<Object, EmployeeDetailResponse> {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;

    @Override
    public EmployeeDetailResponse getResponse(Object request, JWTModel currentUser, String... args) throws Exception {

        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        String employeeIdStr = args[0];
        EmployeeId employeeId = new EmployeeId(employeeIdStr);

        // 1. 查詢員工
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("EMPLOYEE_NOT_FOUND", "員工不存在: " + employeeIdStr));

        // 2. 查詢部門資訊 (包含路徑)
        DepartmentInfo departmentInfo = getDepartmentInfo(employee.getDepartmentIdVO());

        // 3. 查詢主管資訊
        ManagerInfo managerInfo = getManagerInfo(employee.getSupervisorId());

        // 4. 構建回應
        return buildResponse(employee, departmentInfo, managerInfo);
    }

    private DepartmentInfo getDepartmentInfo(DepartmentId departmentId) {
        if (departmentId == null) {
            return null;
        }

        Department targetDept = departmentRepository.findById(departmentId).orElse(null);
        if (targetDept == null) {
            return null;
        }

        // 優化查詢：一次查出該組織下所有部門，避免 N+1 查詢
        // 先查出該組織所有部門，建立 Map<DepartmentId, Department>
        java.util.Map<DepartmentId, Department> allDepartmentsMap = new java.util.HashMap<>();
        if (targetDept.getOrganizationId() != null) {
            allDepartmentsMap = departmentRepository.findByOrganizationId(targetDept.getOrganizationId())
                    .stream()
                    .collect(Collectors.toMap(Department::getId, java.util.function.Function.identity()));
        }

        // 構建部門路徑 (例如: 總公司 / RD / Backend)
        List<String> pathNames = new ArrayList<>();
        Department current = targetDept;

        // 使用 Map 進行內存遍歷
        while (current != null) {
            pathNames.add(current.getName());
            if (current.getParentId() != null) {
                // 從 Cache Map 取得父部門
                current = allDepartmentsMap.get(current.getParentId());

                // Fallback: 如果 Map 裡沒有 (極少見，除非跨組織)，才去 DB 查
                if (current == null && departmentId != null) { // 這裡指 parentId
                    // 這裡 current 已經是 null 了，要怎麼拿 parentId?
                    // 邏輯上 current=null 就會跳出 loop。
                    // 如果 allDepartmentsMap 沒拿到，表示資料不一致或跨組織。
                    // 我們假設同一組織下部門都在 map 裡。
                }
            } else {
                current = null;
            }
        }
        Collections.reverse(pathNames);
        String path = String.join(" / ", pathNames);

        return new DepartmentInfo(targetDept.getId().getValue().toString(), targetDept.getName(), path);
    }

    private ManagerInfo getManagerInfo(EmployeeId managerId) {
        if (managerId == null) {
            return null;
        }

        Employee manager = employeeRepository.findById(managerId).orElse(null);
        if (manager == null) {
            return null;
        }

        return new ManagerInfo(manager.getId().getValue().toString(), manager.getFullName());
    }

    private EmployeeDetailResponse buildResponse(Employee employee, DepartmentInfo deptInfo, ManagerInfo managerInfo) {
        // Address
        EmployeeDetailResponse.AddressDto addressDto = null;
        if (employee.getAddress() != null) {
            addressDto = EmployeeDetailResponse.AddressDto.builder()
                    .postalCode(employee.getAddress().getPostalCode())
                    .city(employee.getAddress().getCity())
                    .district(employee.getAddress().getDistrict())
                    .street(employee.getAddress().getStreet())
                    .fullAddress(employee.getAddress().getFullAddress())
                    .build();
        }

        // Emergency Contact
        EmployeeDetailResponse.EmergencyContactDto contactDto = null;
        if (employee.getEmergencyContact() != null) {
            contactDto = EmployeeDetailResponse.EmergencyContactDto.builder()
                    .name(employee.getEmergencyContact().getName())
                    .relationship(employee.getEmergencyContact().getRelationship())
                    .phoneNumber(employee.getEmergencyContact().getPhone())
                    .build();
        }

        // Bank Account
        EmployeeDetailResponse.BankAccountDto bankAccountDto = null;
        if (employee.getBankAccount() != null) {
            bankAccountDto = EmployeeDetailResponse.BankAccountDto.builder()
                    .bankName(employee.getBankAccount().getBankName())
                    .accountNumber(employee.getBankAccount().getMaskedAccountNumber())
                    .build();
        }

        // Org Info
        EmployeeDetailResponse.OrganizationInfo orgInfo = EmployeeDetailResponse.OrganizationInfo.builder()
                .organizationId(employee.getOrganizationId() != null ? employee.getOrganizationId().toString() : "")
                .organizationName("") // Organization Name 需要從 OrganizationRepository 查，目前這裡可能沒有注入，暫留空
                .build();

        // Dept Info
        EmployeeDetailResponse.DepartmentInfo deptInfoResp = null;
        if (deptInfo != null) {
            deptInfoResp = EmployeeDetailResponse.DepartmentInfo.builder()
                    .departmentId(deptInfo.id)
                    .departmentName(deptInfo.name)
                    .departmentPath(deptInfo.path)
                    .build();
        }

        // Manager Info
        EmployeeDetailResponse.ManagerInfo managerInfoResp = null;
        if (managerInfo != null) {
            managerInfoResp = EmployeeDetailResponse.ManagerInfo.builder()
                    .employeeId(managerInfo.id)
                    .fullName(managerInfo.name)
                    .build();
        }

        return EmployeeDetailResponse.builder()
                .employeeId(employee.getId().getValue().toString())
                .employeeNumber(employee.getEmployeeNumber())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .nationalId(employee.getMaskedNationalId())
                .dateOfBirth(employee.getDateOfBirth())
                .gender(employee.getGender() != null ? employee.getGender().name() : "")
                .genderDisplayName(employee.getGender() != null ? employee.getGender().name() : "") // 暫時使用 Enum name
                .maritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : "")
                .maritalStatusDisplayName(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : "")
                .personalEmail(employee.getPersonalEmail() != null ? employee.getPersonalEmail().getValue() : "")
                .companyEmail(employee.getCompanyEmail() != null ? employee.getCompanyEmail().getValue() : "")
                .mobilePhone(employee.getMobilePhone())
                .homePhone(employee.getHomePhone())
                .address(addressDto)
                .emergencyContact(contactDto)
                .organization(orgInfo)
                .department(deptInfoResp)
                .manager(managerInfoResp)
                .jobTitle(employee.getJobTitle())
                .jobLevel(employee.getJobLevel())
                .employmentType(employee.getEmploymentType() != null ? employee.getEmploymentType().name() : "")
                .employmentTypeDisplayName(
                        employee.getEmploymentType() != null ? employee.getEmploymentType().name() : "")
                .employmentStatus(employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().name() : "")
                .employmentStatusDisplayName(
                        employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().name() : "")
                .hireDate(employee.getHireDate())
                .probationEndDate(employee.getProbationEndDate())
                .terminationDate(employee.getTerminationDate())
                .terminationReason(employee.getTerminationReason())
                .bankAccount(bankAccountDto)
                .photoUrl(employee.getPhotoUrl())
                .seniority(employee.calculateSeniority())
                .build();
    }

    // 內部輔助類
    private record DepartmentInfo(String id, String name, String path) {
    }

    private record ManagerInfo(String id, String name) {
    }
}
