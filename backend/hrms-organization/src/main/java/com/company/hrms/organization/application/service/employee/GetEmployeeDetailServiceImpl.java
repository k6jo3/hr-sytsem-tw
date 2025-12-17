package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.employee.EmployeeDetailResponse;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 取得員工詳情服務實作
 */
@Service("getEmployeeDetailServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeDetailServiceImpl
        implements QueryApiService<Void, EmployeeDetailResponse> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public EmployeeDetailResponse getResponse(Void request,
                                              JWTModel currentUser,
                                              String... args) throws Exception {
        String employeeId = args[0];
        log.info("Getting employee detail: {}", employeeId);

        // 查詢員工
        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        return buildEmployeeDetailResponse(employee);
    }

    private EmployeeDetailResponse buildEmployeeDetailResponse(Employee employee) {
        EmployeeDetailResponse.EmployeeDetailResponseBuilder builder = EmployeeDetailResponse.builder()
                .employeeId(employee.getId().getValue())
                .employeeNumber(employee.getEmployeeNumber())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .englishName(employee.getEnglishName())
                .gender(employee.getGender().name())
                .birthDate(employee.getBirthDate())
                .nationalIdMasked(employee.getNationalId() != null ? employee.getNationalId().getMasked() : null)
                .email(employee.getEmail().getValue())
                .phone(employee.getPhone())
                .departmentId(employee.getDepartmentId().getValue())
                .jobTitle(employee.getJobTitle())
                .jobLevel(employee.getJobLevel())
                .employmentType(employee.getEmploymentType().name())
                .employmentStatus(employee.getEmploymentStatus().name())
                .hireDate(employee.getHireDate())
                .terminationDate(employee.getTerminationDate())
                .maritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : null)
                .supervisorId(employee.getSupervisorId() != null ? employee.getSupervisorId().getValue() : null);

        // 設定地址
        if (employee.getAddress() != null) {
            builder.address(EmployeeDetailResponse.AddressResponse.builder()
                    .postalCode(employee.getAddress().getPostalCode())
                    .city(employee.getAddress().getCity())
                    .district(employee.getAddress().getDistrict())
                    .street(employee.getAddress().getStreet())
                    .fullAddress(employee.getAddress().getFullAddress())
                    .build());
        }

        // 設定緊急聯絡人
        if (employee.getEmergencyContact() != null) {
            builder.emergencyContact(EmployeeDetailResponse.EmergencyContactResponse.builder()
                    .name(employee.getEmergencyContact().getName())
                    .relationship(employee.getEmergencyContact().getRelationship())
                    .phone(employee.getEmergencyContact().getPhone())
                    .build());
        }

        return builder.build();
    }
}
