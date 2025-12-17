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
        String employeeId = args[0];
        log.info("Updating employee: {}", employeeId);

        // 查詢員工
        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        // 記錄舊 Email (用於事件)
        String oldEmail = employee.getEmail().getValue();

        // 更新基本資料
        if (request.getFirstName() != null || request.getLastName() != null) {
            employee.updateBasicInfo(
                    request.getFirstName() != null ? request.getFirstName() : employee.getFirstName(),
                    request.getLastName() != null ? request.getLastName() : employee.getLastName(),
                    request.getEnglishName() != null ? request.getEnglishName() : employee.getEnglishName(),
                    request.getGender() != null ? Gender.valueOf(request.getGender()) : employee.getGender(),
                    request.getBirthDate() != null ? request.getBirthDate() : employee.getBirthDate()
            );
        }

        // 更新聯絡資訊
        if (request.getPhone() != null) {
            employee.updatePhone(request.getPhone());
        }

        // 更新 Email (需檢查唯一性)
        if (request.getEmail() != null && !request.getEmail().equals(oldEmail)) {
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email 已存在: " + request.getEmail());
            }
            employee.updateEmail(new Email(request.getEmail()));

            // 發布 Email 變更事件
            eventPublisher.publishEvent(new EmployeeEmailChangedEvent(
                    employeeId,
                    employee.getEmployeeNumber(),
                    oldEmail,
                    request.getEmail()
            ));
        }

        // 更新婚姻狀態
        if (request.getMaritalStatus() != null) {
            employee.updateMaritalStatus(MaritalStatus.valueOf(request.getMaritalStatus()));
        }

        // 更新地址
        if (request.getAddress() != null) {
            employee.updateAddress(new Address(
                    request.getAddress().getPostalCode(),
                    request.getAddress().getCity(),
                    request.getAddress().getDistrict(),
                    request.getAddress().getStreet()
            ));
        }

        // 更新緊急聯絡人
        if (request.getEmergencyContact() != null) {
            employee.updateEmergencyContact(new EmergencyContact(
                    request.getEmergencyContact().getName(),
                    request.getEmergencyContact().getRelationship(),
                    request.getEmergencyContact().getPhone()
            ));
        }

        // 更新銀行帳戶
        if (request.getBankAccount() != null) {
            employee.updateBankAccount(new BankAccount(
                    request.getBankAccount().getBankCode(),
                    request.getBankAccount().getBranchCode(),
                    request.getBankAccount().getAccountNumber(),
                    request.getBankAccount().getAccountHolderName()
            ));
        }

        // 儲存更新
        employeeRepository.save(employee);

        log.info("Employee updated successfully: {}", employeeId);

        return buildEmployeeDetailResponse(employee);
    }

    private EmployeeDetailResponse buildEmployeeDetailResponse(Employee employee) {
        return EmployeeDetailResponse.builder()
                .employeeId(employee.getId().getValue())
                .employeeNumber(employee.getEmployeeNumber())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFullName())
                .englishName(employee.getEnglishName())
                .gender(employee.getGender().name())
                .birthDate(employee.getBirthDate())
                .email(employee.getEmail().getValue())
                .phone(employee.getPhone())
                .departmentId(employee.getDepartmentId().getValue())
                .jobTitle(employee.getJobTitle())
                .jobLevel(employee.getJobLevel())
                .employmentType(employee.getEmploymentType().name())
                .employmentStatus(employee.getEmploymentStatus().name())
                .hireDate(employee.getHireDate())
                .maritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : null)
                .build();
    }
}
