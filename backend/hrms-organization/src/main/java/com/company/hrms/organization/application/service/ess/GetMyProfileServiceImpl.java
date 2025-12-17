package com.company.hrms.organization.application.service.ess;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * 取得個人資料服務實作 (員工自助)
 */
@Service("getMyProfileServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetMyProfileServiceImpl
        implements QueryApiService<Void, MyProfileResponse> {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;

    @Override
    public MyProfileResponse getResponse(Void request,
                                         JWTModel currentUser,
                                         String... args) throws Exception {
        log.info("Getting my profile for user: {}", currentUser.getUserId());

        // 從 JWT 取得員工ID (假設 userId 對應員工ID 或需要額外查詢)
        String employeeId = currentUser.getEmployeeId();
        if (employeeId == null) {
            throw new IllegalStateException("使用者未關聯員工資料");
        }

        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在"));

        // 取得部門資訊
        String departmentName = null;
        if (employee.getDepartmentId() != null) {
            departmentName = departmentRepository.findById(employee.getDepartmentId())
                    .map(Department::getName)
                    .orElse(null);
        }

        // 取得主管姓名
        String supervisorName = null;
        if (employee.getSupervisorId() != null) {
            supervisorName = employeeRepository.findById(employee.getSupervisorId())
                    .map(Employee::getFullName)
                    .orElse(null);
        }

        // 計算年資
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

        // 設定地址
        if (employee.getAddress() != null) {
            builder.address(MyProfileResponse.AddressResponse.builder()
                    .postalCode(employee.getAddress().getPostalCode())
                    .city(employee.getAddress().getCity())
                    .district(employee.getAddress().getDistrict())
                    .street(employee.getAddress().getStreet())
                    .fullAddress(employee.getAddress().getFullAddress())
                    .build());
        }

        // 設定緊急聯絡人
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
        if (hireDate == null) {
            return "-";
        }

        Period period = Period.between(hireDate, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();

        if (years > 0) {
            return years + " 年 " + months + " 個月";
        } else {
            return months + " 個月";
        }
    }
}
