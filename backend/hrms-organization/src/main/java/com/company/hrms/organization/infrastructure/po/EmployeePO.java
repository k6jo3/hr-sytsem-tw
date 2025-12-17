package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 員工持久化對象
 */
@Data
public class EmployeePO {

    private UUID employeeId;
    private String employeeNumber;

    // 基本資料
    private String firstName;
    private String lastName;
    private String fullName;
    private String nationalId;  // 加密儲存
    private LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;

    // 聯絡方式
    private String personalEmail;
    private String companyEmail;
    private String mobilePhone;
    private String homePhone;

    // 地址 (JSON)
    private String address;

    // 緊急聯絡人 (JSON)
    private String emergencyContact;

    // 組織關係
    private UUID organizationId;
    private UUID departmentId;
    private UUID managerId;

    // 職務資訊
    private String jobTitle;
    private String jobLevel;
    private String employmentType;
    private String employmentStatus;

    // 到離職資訊
    private LocalDate hireDate;
    private LocalDate probationEndDate;
    private LocalDate terminationDate;
    private String terminationReason;

    // 銀行資訊 (JSON，加密)
    private String bankAccount;

    // 照片
    private String photoUrl;

    // 審計
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
