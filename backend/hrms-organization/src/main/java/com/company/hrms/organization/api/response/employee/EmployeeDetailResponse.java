package com.company.hrms.organization.api.response.employee;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 員工詳細資料回應 DTO
 */
@Data
@Builder
public class EmployeeDetailResponse {

    private String employeeId;
    private String employeeNumber;

    // 基本資料
    private String firstName;
    private String lastName;
    private String fullName;
    private String nationalId;  // 遮罩後
    private LocalDate dateOfBirth;
    private String gender;
    private String genderDisplayName;
    private String maritalStatus;
    private String maritalStatusDisplayName;

    // 聯絡方式
    private String personalEmail;
    private String companyEmail;
    private String mobilePhone;
    private String homePhone;
    private AddressDto address;
    private EmergencyContactDto emergencyContact;

    // 組織關係
    private OrganizationInfo organization;
    private DepartmentInfo department;
    private ManagerInfo manager;

    // 職務資訊
    private String jobTitle;
    private String jobLevel;
    private String employmentType;
    private String employmentTypeDisplayName;
    private String employmentStatus;
    private String employmentStatusDisplayName;

    // 到離職資訊
    private LocalDate hireDate;
    private LocalDate probationEndDate;
    private LocalDate terminationDate;
    private String terminationReason;

    // 銀行資訊
    private BankAccountDto bankAccount;

    // 照片
    private String photoUrl;

    // 計算欄位
    private Integer seniority;

    @Data
    @Builder
    public static class AddressDto {
        private String postalCode;
        private String city;
        private String district;
        private String street;
        private String fullAddress;
    }

    @Data
    @Builder
    public static class EmergencyContactDto {
        private String name;
        private String relationship;
        private String phoneNumber;
    }

    @Data
    @Builder
    public static class OrganizationInfo {
        private String organizationId;
        private String organizationName;
    }

    @Data
    @Builder
    public static class DepartmentInfo {
        private String departmentId;
        private String departmentName;
        private String departmentPath;
    }

    @Data
    @Builder
    public static class ManagerInfo {
        private String employeeId;
        private String fullName;
    }

    @Data
    @Builder
    public static class BankAccountDto {
        private String bankName;
        private String accountNumber;  // 遮罩後
    }
}
