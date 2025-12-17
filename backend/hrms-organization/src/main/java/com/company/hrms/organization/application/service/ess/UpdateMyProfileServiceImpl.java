package com.company.hrms.organization.application.service.ess;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.ess.UpdateMyProfileRequest;
import com.company.hrms.organization.api.response.ess.MyProfileResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * 更新個人資料服務實作 (員工自助)
 */
@Service("updateMyProfileServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UpdateMyProfileServiceImpl
        implements CommandApiService<UpdateMyProfileRequest, MyProfileResponse> {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;

    @Override
    public MyProfileResponse execCommand(UpdateMyProfileRequest request,
                                         JWTModel currentUser,
                                         String... args) throws Exception {
        log.info("Updating my profile for user: {}", currentUser.getUserId());

        String employeeId = currentUser.getEmployeeId();
        if (employeeId == null) {
            throw new IllegalStateException("使用者未關聯員工資料");
        }

        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在"));

        // 更新電話
        if (request.getPhone() != null) {
            employee.updatePhone(request.getPhone());
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

        log.info("My profile updated successfully for employee: {}", employeeId);

        return buildMyProfileResponse(employee);
    }

    private MyProfileResponse buildMyProfileResponse(Employee employee) {
        String departmentName = departmentRepository.findById(employee.getDepartmentId())
                .map(Department::getName)
                .orElse(null);

        String supervisorName = null;
        if (employee.getSupervisorId() != null) {
            supervisorName = employeeRepository.findById(employee.getSupervisorId())
                    .map(Employee::getFullName)
                    .orElse(null);
        }

        String seniority = calculateSeniority(employee.getHireDate());

        MyProfileResponse.MyProfileResponseBuilder builder = MyProfileResponse.builder()
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
                .maritalStatus(employee.getMaritalStatus() != null ? employee.getMaritalStatus().name() : null)
                .departmentId(employee.getDepartmentId().getValue())
                .departmentName(departmentName)
                .jobTitle(employee.getJobTitle())
                .jobLevel(employee.getJobLevel())
                .employmentType(employee.getEmploymentType().name())
                .employmentStatus(employee.getEmploymentStatus().name())
                .hireDate(employee.getHireDate())
                .seniority(seniority)
                .supervisorId(employee.getSupervisorId() != null ? employee.getSupervisorId().getValue() : null)
                .supervisorName(supervisorName);

        if (employee.getAddress() != null) {
            builder.address(MyProfileResponse.AddressResponse.builder()
                    .postalCode(employee.getAddress().getPostalCode())
                    .city(employee.getAddress().getCity())
                    .district(employee.getAddress().getDistrict())
                    .street(employee.getAddress().getStreet())
                    .fullAddress(employee.getAddress().getFullAddress())
                    .build());
        }

        if (employee.getEmergencyContact() != null) {
            builder.emergencyContact(MyProfileResponse.EmergencyContactResponse.builder()
                    .name(employee.getEmergencyContact().getName())
                    .relationship(employee.getEmergencyContact().getRelationship())
                    .phone(employee.getEmergencyContact().getPhone())
                    .build());
        }

        return builder.build();
    }

    private String calculateSeniority(LocalDate hireDate) {
        if (hireDate == null) return "-";
        Period period = Period.between(hireDate, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();
        if (years > 0) {
            return years + " 年 " + months + " 個月";
        }
        return months + " 個月";
    }
}
