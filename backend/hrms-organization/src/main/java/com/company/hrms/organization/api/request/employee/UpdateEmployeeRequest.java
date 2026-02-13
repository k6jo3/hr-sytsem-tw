package com.company.hrms.organization.api.request.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新員工請求 DTO
 */
@Data
public class UpdateEmployeeRequest {

    @Size(max = 100, message = "姓長度不可超過100字元")
    private String lastName;

    @Size(max = 100, message = "名長度不可超過100字元")
    private String firstName;

    private String maritalStatus;

    @Email(message = "個人Email格式不正確")
    private String personalEmail;

    @Email(message = "公司Email格式不正確")
    private String companyEmail;

    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String mobilePhone;

    private String homePhone;

    // 地址
    private CreateEmployeeRequest.AddressDto address;

    // 緊急聯絡人
    private CreateEmployeeRequest.EmergencyContactDto emergencyContact;

    // 職務資訊
    private String jobTitle;
    private String jobLevel;
    private String managerId;

    // 銀行資訊
    private CreateEmployeeRequest.BankAccountDto bankAccount;

    // 照片 URL
    private String photoUrl;
}
