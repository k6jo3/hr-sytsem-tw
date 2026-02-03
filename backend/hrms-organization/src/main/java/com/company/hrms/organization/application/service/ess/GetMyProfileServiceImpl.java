package com.company.hrms.organization.application.service.ess;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.ess.MyProfileResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢個人資料服務實作 (員工自助)
 */
@Service("getMyProfileServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetMyProfileServiceImpl implements QueryApiService<Void, MyProfileResponse> {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;

    @Override
    public MyProfileResponse getResponse(Void request, JWTModel currentUser, String... args) throws Exception {
        String employeeIdStr = currentUser.getUserId();
        log.info("查詢個人資料: userId={}", employeeIdStr);

        EmployeeId employeeId = new EmployeeId(employeeIdStr);

        // 1. 查詢員工
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("EMPLOYEE_NOT_FOUND", "找不到對應的員工資料: " + employeeIdStr));

        // 2. 查詢部門與主管名稱
        String departmentName = "";
        if (employee.getDepartmentId() != null) {
            departmentName = departmentRepository.findById(employee.getDepartmentIdVO())
                    .map(Department::getName)
                    .orElse("");
        }

        String supervisorName = "";
        if (employee.getManagerId() != null) {
            supervisorName = employeeRepository.findById(employee.getSupervisorId())
                    .map(Employee::getFullName)
                    .orElse("");
        }

        // 3. 轉換為 Response
        return mapToResponse(employee, departmentName, supervisorName);
    }

    private MyProfileResponse mapToResponse(Employee employee, String departmentName, String supervisorName) {
        return MyProfileResponse.builder()
                .employeeId(employee.getId().getValue().toString())
                .employeeNumber(employee.getEmployeeNumber())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .gender(employee.getGender() != null ? employee.getGender().name() : null)
                .birthDate(employee.getDateOfBirth())
                .email(employee.getCompanyEmail() != null ? employee.getCompanyEmail().getValue() : null)
                .phone(employee.getMobilePhone())
                .maritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : null)
                .address(employee.getAddress() != null ? MyProfileResponse.AddressResponse.builder()
                        .postalCode(employee.getAddress().getPostalCode())
                        .city(employee.getAddress().getCity())
                        .district(employee.getAddress().getDistrict())
                        .street(employee.getAddress().getStreet())
                        .fullAddress(employee.getAddress().getFullAddress())
                        .build() : null)
                .emergencyContact(
                        employee.getEmergencyContact() != null ? MyProfileResponse.EmergencyContactResponse.builder()
                                .name(employee.getEmergencyContact().getName())
                                .relationship(employee.getEmergencyContact().getRelationship())
                                .phone(employee.getEmergencyContact().getPhone())
                                .build() : null)
                .departmentId(employee.getDepartmentId() != null ? employee.getDepartmentId().toString() : null)
                .departmentName(departmentName)
                .jobTitle(employee.getJobTitle())
                .jobLevel(employee.getJobLevel())
                .employmentType(employee.getEmploymentType() != null ? employee.getEmploymentType().name() : null)
                .employmentStatus(employee.getEmploymentStatus() != null ? employee.getEmploymentStatus().name() : null)
                .hireDate(employee.getHireDate())
                .seniority(employee.calculateSeniority() + " 年")
                .supervisorId(employee.getManagerId() != null ? employee.getManagerId().toString() : null)
                .supervisorName(supervisorName)
                .build();
    }
}
