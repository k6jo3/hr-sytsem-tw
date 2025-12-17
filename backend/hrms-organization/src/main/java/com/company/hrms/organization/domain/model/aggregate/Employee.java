package com.company.hrms.organization.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 員工聚合根
 * HR系統的核心聚合根，管理員工全生命週期
 */
@Getter
@Builder
public class Employee {

    /**
     * 員工 ID
     */
    private final EmployeeId id;

    /**
     * 員工編號 (唯一)
     */
    private String employeeNumber;

    // ==================== 基本資料 ====================

    /**
     * 姓
     */
    private String firstName;

    /**
     * 名
     */
    private String lastName;

    /**
     * 全名
     */
    private String fullName;

    /**
     * 身分證號 (加密儲存)
     */
    private NationalId nationalId;

    /**
     * 出生日期
     */
    private LocalDate dateOfBirth;

    /**
     * 性別
     */
    private Gender gender;

    /**
     * 婚姻狀況
     */
    private MaritalStatus maritalStatus;

    // ==================== 聯絡方式 ====================

    /**
     * 個人 Email
     */
    private Email personalEmail;

    /**
     * 公司 Email (唯一)
     */
    private Email companyEmail;

    /**
     * 手機
     */
    private String mobilePhone;

    /**
     * 住家電話
     */
    private String homePhone;

    /**
     * 地址
     */
    private Address address;

    /**
     * 緊急聯絡人
     */
    private EmergencyContact emergencyContact;

    // ==================== 組織關係 ====================

    /**
     * 所屬組織 ID
     */
    private UUID organizationId;

    /**
     * 所屬部門 ID
     */
    private UUID departmentId;

    /**
     * 直屬主管 ID
     */
    private UUID managerId;

    // ==================== 職務資訊 ====================

    /**
     * 職稱
     */
    private String jobTitle;

    /**
     * 職等
     */
    private String jobLevel;

    /**
     * 雇用類型
     */
    private EmploymentType employmentType;

    /**
     * 在職狀態
     */
    private EmploymentStatus employmentStatus;

    // ==================== 到離職資訊 ====================

    /**
     * 到職日期
     */
    private LocalDate hireDate;

    /**
     * 試用期結束日期
     */
    private LocalDate probationEndDate;

    /**
     * 離職日期
     */
    private LocalDate terminationDate;

    /**
     * 離職原因
     */
    private String terminationReason;

    // ==================== 銀行資訊 ====================

    /**
     * 銀行帳戶 (加密儲存)
     */
    private BankAccount bankAccount;

    /**
     * 照片 URL
     */
    private String photoUrl;

    // ==================== 審計 ====================

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    // ==================== 工廠方法 ====================

    /**
     * 員工到職 - 建立新員工
     * @param employeeNumber 員工編號
     * @param firstName 姓
     * @param lastName 名
     * @param nationalId 身分證號
     * @param dateOfBirth 出生日期
     * @param gender 性別
     * @param companyEmail 公司 Email
     * @param mobilePhone 手機
     * @param organizationId 組織 ID
     * @param departmentId 部門 ID
     * @param jobTitle 職稱
     * @param employmentType 雇用類型
     * @param hireDate 到職日期
     * @param probationMonths 試用期月數
     * @return 新的 Employee 實例
     */
    public static Employee onboard(
            String employeeNumber,
            String firstName,
            String lastName,
            String nationalId,
            LocalDate dateOfBirth,
            Gender gender,
            String companyEmail,
            String mobilePhone,
            UUID organizationId,
            UUID departmentId,
            String jobTitle,
            EmploymentType employmentType,
            LocalDate hireDate,
            int probationMonths) {

        validateEmployeeNumber(employeeNumber);
        validateName(firstName, lastName);

        if (organizationId == null) {
            throw new DomainException("ORG_ID_REQUIRED", "組織 ID 不可為空");
        }
        if (departmentId == null) {
            throw new DomainException("DEPT_ID_REQUIRED", "部門 ID 不可為空");
        }
        if (hireDate == null) {
            throw new DomainException("HIRE_DATE_REQUIRED", "到職日期不可為空");
        }

        String fullName = lastName + firstName;
        LocalDate probationEndDate = probationMonths > 0 ? hireDate.plusMonths(probationMonths) : null;
        EmploymentStatus initialStatus = probationMonths > 0 ? EmploymentStatus.PROBATION : EmploymentStatus.ACTIVE;

        return Employee.builder()
                .id(EmployeeId.generate())
                .employeeNumber(employeeNumber)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(fullName)
                .nationalId(new NationalId(nationalId))
                .dateOfBirth(dateOfBirth)
                .gender(gender)
                .companyEmail(new Email(companyEmail))
                .mobilePhone(mobilePhone)
                .organizationId(organizationId)
                .departmentId(departmentId)
                .jobTitle(jobTitle)
                .employmentType(employmentType)
                .employmentStatus(initialStatus)
                .hireDate(hireDate)
                .probationEndDate(probationEndDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 試用期轉正
     * @throws DomainException 若不是試用期狀態
     */
    public void completeProbation() {
        if (this.employmentStatus != EmploymentStatus.PROBATION) {
            throw new DomainException("NOT_IN_PROBATION", "員工不在試用期");
        }
        this.employmentStatus = EmploymentStatus.ACTIVE;
        this.probationEndDate = LocalDate.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 部門調動
     * @param newDepartmentId 新部門 ID
     * @param newManagerId 新主管 ID (可為 null)
     */
    public void transferDepartment(UUID newDepartmentId, UUID newManagerId) {
        if (newDepartmentId == null) {
            throw new DomainException("NEW_DEPT_REQUIRED", "新部門 ID 不可為空");
        }
        if (this.employmentStatus.isTerminated()) {
            throw new DomainException("EMPLOYEE_TERMINATED", "離職員工不可調動");
        }
        this.departmentId = newDepartmentId;
        this.managerId = newManagerId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 升遷
     * @param newJobTitle 新職稱
     * @param newJobLevel 新職等
     */
    public void promote(String newJobTitle, String newJobLevel) {
        if (this.employmentStatus.isTerminated()) {
            throw new DomainException("EMPLOYEE_TERMINATED", "離職員工不可升遷");
        }
        if (newJobTitle != null && !newJobTitle.isBlank()) {
            this.jobTitle = newJobTitle;
        }
        if (newJobLevel != null && !newJobLevel.isBlank()) {
            this.jobLevel = newJobLevel;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 離職
     * @param terminationDate 離職日期
     * @param reason 離職原因
     */
    public void terminate(LocalDate terminationDate, String reason) {
        if (this.employmentStatus.isTerminated()) {
            throw new DomainException("ALREADY_TERMINATED", "員工已離職");
        }
        if (terminationDate == null) {
            throw new DomainException("TERMINATION_DATE_REQUIRED", "離職日期不可為空");
        }
        if (terminationDate.isBefore(this.hireDate)) {
            throw new DomainException("INVALID_TERMINATION_DATE", "離職日期不可早於到職日期");
        }
        this.employmentStatus = EmploymentStatus.TERMINATED;
        this.terminationDate = terminationDate;
        this.terminationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新個人資料
     * @param personalEmail 個人 Email
     * @param mobilePhone 手機
     * @param address 地址
     * @param emergencyContact 緊急聯絡人
     */
    public void updatePersonalInfo(String personalEmail, String mobilePhone,
                                    Address address, EmergencyContact emergencyContact) {
        if (personalEmail != null && !personalEmail.isBlank()) {
            this.personalEmail = new Email(personalEmail);
        }
        if (mobilePhone != null && !mobilePhone.isBlank()) {
            this.mobilePhone = mobilePhone;
        }
        if (address != null) {
            this.address = address;
        }
        if (emergencyContact != null) {
            this.emergencyContact = emergencyContact;
        }
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新公司 Email
     * @param companyEmail 公司 Email
     */
    public void updateCompanyEmail(String companyEmail) {
        if (companyEmail == null || companyEmail.isBlank()) {
            throw new DomainException("COMPANY_EMAIL_REQUIRED", "公司 Email 不可為空");
        }
        this.companyEmail = new Email(companyEmail);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新銀行帳戶
     * @param bankAccount 銀行帳戶
     */
    public void updateBankAccount(BankAccount bankAccount) {
        if (bankAccount != null) {
            bankAccount.validate();
        }
        this.bankAccount = bankAccount;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新職務資訊
     * @param jobTitle 職稱
     * @param jobLevel 職等
     * @param managerId 主管 ID
     */
    public void updateJobInfo(String jobTitle, String jobLevel, UUID managerId) {
        if (jobTitle != null && !jobTitle.isBlank()) {
            this.jobTitle = jobTitle;
        }
        if (jobLevel != null && !jobLevel.isBlank()) {
            this.jobLevel = jobLevel;
        }
        this.managerId = managerId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新照片
     * @param photoUrl 照片 URL
     */
    public void updatePhoto(String photoUrl) {
        this.photoUrl = photoUrl;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 開始育嬰留停
     */
    public void startParentalLeave() {
        if (!this.employmentStatus.isActive()) {
            throw new DomainException("NOT_ACTIVE", "非在職員工不可申請育嬰留停");
        }
        this.employmentStatus = EmploymentStatus.PARENTAL_LEAVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 開始留職停薪
     */
    public void startUnpaidLeave() {
        if (!this.employmentStatus.isActive()) {
            throw new DomainException("NOT_ACTIVE", "非在職員工不可申請留職停薪");
        }
        this.employmentStatus = EmploymentStatus.UNPAID_LEAVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 復職
     */
    public void returnFromLeave() {
        if (!this.employmentStatus.isOnLeave()) {
            throw new DomainException("NOT_ON_LEAVE", "員工不在留停狀態");
        }
        this.employmentStatus = EmploymentStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== 查詢方法 ====================

    /**
     * 是否在職
     * @return 是否在職
     */
    public boolean isActive() {
        return this.employmentStatus.isActive();
    }

    /**
     * 是否試用期
     * @return 是否試用期
     */
    public boolean isProbation() {
        return this.employmentStatus.isProbation();
    }

    /**
     * 是否已離職
     * @return 是否已離職
     */
    public boolean isTerminated() {
        return this.employmentStatus.isTerminated();
    }

    /**
     * 是否留停中
     * @return 是否留停
     */
    public boolean isOnLeave() {
        return this.employmentStatus.isOnLeave();
    }

    /**
     * 計算年資 (年)
     * @return 年資
     */
    public int calculateSeniority() {
        LocalDate endDate = this.terminationDate != null ? this.terminationDate : LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.YEARS.between(this.hireDate, endDate);
    }

    /**
     * 取得遮罩後的身分證號
     * @return 遮罩後的身分證號
     */
    public String getMaskedNationalId() {
        return this.nationalId != null ? this.nationalId.getMaskedValue() : "";
    }

    // ==================== 驗證方法 ====================

    private static void validateEmployeeNumber(String number) {
        if (number == null || number.isBlank()) {
            throw new DomainException("EMPLOYEE_NUMBER_REQUIRED", "員工編號不可為空");
        }
        if (number.length() > 50) {
            throw new DomainException("EMPLOYEE_NUMBER_TOO_LONG", "員工編號長度不可超過50字元");
        }
    }

    private static void validateName(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new DomainException("FIRST_NAME_REQUIRED", "名不可為空");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new DomainException("LAST_NAME_REQUIRED", "姓不可為空");
        }
    }
}
