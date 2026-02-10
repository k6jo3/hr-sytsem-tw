package com.company.hrms.organization.infrastructure.po;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 員工持久化對象
 */
@Data
@Entity
@Table(name = "employees")
public class EmployeePO {

    @Id
    @Column(name = "employee_id")
    private String id;

    @Column(name = "employee_number")
    private String employeeNumber;

    // 基本資料
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name")
    private String fullName; // 用於模糊查詢

    @Column(name = "english_name")
    private String englishName;

    @Column(name = "national_id")
    private String nationalId; // 加密儲存

    @Column(name = "date_of_birth")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String gender;

    @Column(name = "marital_status")
    private String maritalStatus;

    // 聯絡方式
    @Column(name = "company_email")
    private String email; // 公司 Email

    @Column(name = "mobile_phone")
    private String phone; // 手機

    // 地址 (展開從 JSONB，暫不實作)
    private String addressPostalCode;
    private String addressCity;
    private String addressDistrict;
    private String addressStreet;

    // 緊急聯絡人 (展開從 JSONB，暫不實作)
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;

    // 銀行資訊 (展開從 JSONB，暫不實作)
    private String bankCode;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String bankAccountHolderName;

    // 組織關係
    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "manager_id")
    private String supervisorId;

    // 職務資訊
    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "job_level")
    private String jobLevel;

    @Column(name = "employment_type")
    private String employmentType;

    @Column(name = "employment_status")
    private String employmentStatus;

    // 到離職資訊
    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "termination_reason")
    private String terminationReason;

    // 審計
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
