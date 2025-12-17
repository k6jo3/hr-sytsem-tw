package com.company.hrms.organization.api.request.employee;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 新增員工請求 DTO
 */
@Data
public class CreateEmployeeRequest {

    @NotBlank(message = "員工編號不可為空")
    @Size(max = 50, message = "員工編號長度不可超過50字元")
    private String employeeNumber;

    @NotBlank(message = "姓不可為空")
    @Size(max = 100, message = "姓長度不可超過100字元")
    private String lastName;

    @NotBlank(message = "名不可為空")
    @Size(max = 100, message = "名長度不可超過100字元")
    private String firstName;

    @NotBlank(message = "身分證字號不可為空")
    @Pattern(regexp = "^[A-Z][12]\\d{8}$", message = "身分證字號格式不正確")
    private String nationalId;

    @NotNull(message = "出生日期不可為空")
    @Past(message = "出生日期必須是過去的日期")
    private LocalDate dateOfBirth;

    @NotBlank(message = "性別不可為空")
    private String gender;

    private String maritalStatus;

    @Email(message = "個人Email格式不正確")
    private String personalEmail;

    @NotBlank(message = "公司Email不可為空")
    @Email(message = "公司Email格式不正確")
    private String companyEmail;

    @NotBlank(message = "手機號碼不可為空")
    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String mobilePhone;

    private String homePhone;

    // 地址
    private AddressDto address;

    // 緊急聯絡人
    private EmergencyContactDto emergencyContact;

    // 組織關係
    @NotBlank(message = "組織ID不可為空")
    private String organizationId;

    @NotBlank(message = "部門ID不可為空")
    private String departmentId;

    private String managerId;

    // 職務資訊
    private String jobTitle;
    private String jobLevel;

    @NotBlank(message = "雇用類型不可為空")
    private String employmentType;

    @NotNull(message = "到職日期不可為空")
    private LocalDate hireDate;

    @Min(value = 0, message = "試用期月數不可為負數")
    @Max(value = 12, message = "試用期月數不可超過12個月")
    private Integer probationMonths = 3;

    // 銀行資訊
    private BankAccountDto bankAccount;

    @Data
    public static class AddressDto {
        private String postalCode;
        private String city;
        private String district;
        private String street;
    }

    @Data
    public static class EmergencyContactDto {
        private String name;
        private String relationship;
        private String phoneNumber;
    }

    @Data
    public static class BankAccountDto {
        private String bankCode;
        private String bankName;
        private String branchCode;
        private String accountNumber;
        private String accountHolderName;
    }
}
