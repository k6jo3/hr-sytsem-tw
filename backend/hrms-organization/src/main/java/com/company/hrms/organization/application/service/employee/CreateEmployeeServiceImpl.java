package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.CreateEmployeeRequest;
import com.company.hrms.organization.api.response.employee.CreateEmployeeResponse;
import com.company.hrms.organization.domain.event.EmployeeCreatedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 新增員工服務實作
 */
@Service("createEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateEmployeeServiceImpl
        implements CommandApiService<CreateEmployeeRequest, CreateEmployeeResponse> {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public CreateEmployeeResponse execCommand(CreateEmployeeRequest request,
                                               JWTModel currentUser,
                                               String... args) throws Exception {
        log.info("Creating employee: {}", request.getEmployeeNumber());

        // 驗證員工編號唯一性
        if (employeeRepository.existsByEmployeeNumber(request.getEmployeeNumber())) {
            throw new IllegalArgumentException("員工編號已存在: " + request.getEmployeeNumber());
        }

        // 驗證 Email 唯一性
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email 已存在: " + request.getEmail());
        }

        // 驗證身分證號唯一性
        if (request.getNationalId() != null &&
                employeeRepository.existsByNationalId(request.getNationalId())) {
            throw new IllegalArgumentException("身分證號已存在");
        }

        // 驗證部門存在
        DepartmentId departmentId = new DepartmentId(request.getDepartmentId());
        if (!departmentRepository.existsById(departmentId)) {
            throw new IllegalArgumentException("部門不存在: " + request.getDepartmentId());
        }

        // 建立員工 Aggregate
        Employee employee = Employee.create(
                request.getEmployeeNumber(),
                request.getFirstName(),
                request.getLastName(),
                request.getEnglishName(),
                Gender.valueOf(request.getGender()),
                request.getBirthDate(),
                request.getNationalId() != null ? new NationalId(request.getNationalId()) : null,
                new Email(request.getEmail()),
                request.getPhone(),
                departmentId,
                request.getJobTitle(),
                request.getJobLevel(),
                EmploymentType.valueOf(request.getEmploymentType()),
                request.getHireDate(),
                request.getSupervisorId() != null ? new EmployeeId(request.getSupervisorId()) : null
        );

        // 設定其他可選欄位
        if (request.getMaritalStatus() != null) {
            employee.updateMaritalStatus(MaritalStatus.valueOf(request.getMaritalStatus()));
        }

        if (request.getAddress() != null) {
            employee.updateAddress(new Address(
                    request.getAddress().getPostalCode(),
                    request.getAddress().getCity(),
                    request.getAddress().getDistrict(),
                    request.getAddress().getStreet()
            ));
        }

        if (request.getEmergencyContact() != null) {
            employee.updateEmergencyContact(new EmergencyContact(
                    request.getEmergencyContact().getName(),
                    request.getEmergencyContact().getRelationship(),
                    request.getEmergencyContact().getPhone()
            ));
        }

        if (request.getBankAccount() != null) {
            employee.updateBankAccount(new BankAccount(
                    request.getBankAccount().getBankCode(),
                    request.getBankAccount().getBranchCode(),
                    request.getBankAccount().getAccountNumber(),
                    request.getBankAccount().getAccountHolderName()
            ));
        }

        // 儲存員工
        employeeRepository.save(employee);

        // 發布領域事件
        eventPublisher.publishEvent(new EmployeeCreatedEvent(
                employee.getId().getValue(),
                employee.getEmployeeNumber(),
                employee.getFullName(),
                employee.getEmail().getValue(),
                employee.getDepartmentId().getValue(),
                employee.getHireDate()
        ));

        log.info("Employee created successfully: {}", employee.getId().getValue());

        return CreateEmployeeResponse.success(
                employee.getId().getValue(),
                employee.getEmployeeNumber(),
                employee.getFullName()
        );
    }
}
