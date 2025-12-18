package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 員工持久化對象
 */
@Data
public class EmployeePO {

    private String id;
    private String employeeNumber;

    // 基本資料
    private String firstName;
    private String lastName;
    private String englishName;
    private String nationalId;  // 加密儲存
    private LocalDate birthDate;
    private String gender;
    private String maritalStatus;

    // 聯絡方式
    private String email;  // 公司 Email
    private String phone;  // 手機

    // 地址
    private String addressPostalCode;
    private String addressCity;
    private String addressDistrict;
    private String addressStreet;

    // 緊急聯絡人
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;

    // 銀行資訊
    private String bankCode;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String bankAccountHolderName;

    // 組織關係
    private String departmentId;
    private String supervisorId;

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

    // 審計
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
