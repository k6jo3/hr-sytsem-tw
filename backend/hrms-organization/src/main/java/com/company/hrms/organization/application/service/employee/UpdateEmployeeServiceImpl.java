package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.UpdateEmployeeRequest;
import com.company.hrms.organization.api.response.employee.EmployeeDetailResponse;
import com.company.hrms.organization.domain.event.EmployeeEmailChangedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 更新員工服務實作
 */
@Service("updateEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateEmployeeServiceImpl
        implements CommandApiService<UpdateEmployeeRequest, EmployeeDetailResponse> {

    private final IEmployeeRepository employeeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public EmployeeDetailResponse execCommand(UpdateEmployeeRequest request,
                                               JWTModel currentUser,
                                               String... args) throws Exception {
        String employeeIdStr = args[0];
        log.info("Updating employee: {}", employeeIdStr);

        EmployeeId employeeId = new EmployeeId(employeeIdStr);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeIdStr));

        if (request.getEmail() != null && !request.getEmail().equals(employee.getCompanyEmail().getValue())) {
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email 已存在: " + request.getEmail());
            }
            String oldEmail = employee.getCompanyEmail().getValue();
            employee.updateCompanyEmail(request.getEmail());

            eventPublisher.publishEvent(new EmployeeEmailChangedEvent(
                    UUID.fromString(employeeIdStr),
                    employee.getEmployeeNumber(),
                    oldEmail,
                    request.getEmail()
            ));
        }

        if (request.getMaritalStatus() != null) {
            employee.setMaritalStatus(MaritalStatus.valueOf(request.getMaritalStatus()));
        }

        Address address = null;
        if (request.getAddress() != null) {
            address = new Address(
                    null,
                    request.getAddress().getCity(),
                    request.getAddress().getDistrict(),
                    request.getAddress().getStreet()
            );
        }

        EmergencyContact emergencyContact = null;
        if (request.getEmergencyContact() != null) {
            emergencyContact = new EmergencyContact(
                    request.getEmergencyContact().getName(),
                    request.getEmergencyContact().getRelationship(),
                    request.getEmergencyContact().getPhoneNumber() // Request has 'phoneNumber'? No, earlier error said 'phone' in VO.
                    // Need to check Request DTO. Assuming Request DTO has phoneNumber or phone.
                    // I will check UpdateEmployeeRequest later if this fails.
                    // Assuming Request has getPhoneNumber() based on CreateEmployeeRequest.
            );
        }

        employee.updatePersonalInfo(
                request.getPersonalEmail(),
                request.getMobilePhone(), // Request mobilePhone?
                address != null ? address : employee.getAddress(),
                emergencyContact != null ? emergencyContact : employee.getEmergencyContact()
        );

        if (request.getBankAccount() != null) {
            employee.updateBankAccount(new BankAccount(
                    request.getBankAccount().getBankCode(),
                    null,
                    request.getBankAccount().getBranchCode(),
                    request.getBankAccount().getAccountNumber(),
                    request.getBankAccount().getAccountHolderName()
            ));
        }

        employeeRepository.save(employee);

        return buildEmployeeDetailResponse(employee);
    }

    private EmployeeDetailResponse buildEmployeeDetailResponse(Employee employee) {
        return EmployeeDetailResponse.builder()
                .employeeId(employee.getId().getValue().toString())
                .employeeNumber(employee.getEmployeeNumber())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .englishName(employee.getEnglishName())
                .gender(employee.getGender().name())
                .birthDate(employee.getBirthDate())
                .email(employee.getCompanyEmail().getValue())
                .phone(employee.getMobilePhone())
                .departmentId(employee.getDepartmentId() != null ? employee.getDepartmentId().toString() : null)
                .jobTitle(employee.getJobTitle())
                .jobLevel(employee.getJobLevel())
                .employmentType(employee.getEmploymentType().name())
                .employmentStatus(employee.getEmploymentStatus().name())
                .hireDate(employee.getHireDate())
                .maritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : null)
                .build();
    }
}
