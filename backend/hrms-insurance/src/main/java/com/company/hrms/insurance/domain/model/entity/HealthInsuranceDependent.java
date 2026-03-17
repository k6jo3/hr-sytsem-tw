package com.company.hrms.insurance.domain.model.entity;

import java.time.LocalDate;

import com.company.hrms.insurance.domain.model.valueobject.DependentId;
import com.company.hrms.insurance.domain.model.valueobject.DependentType;

import lombok.Getter;

/**
 * 健保眷屬 Entity
 * 屬於 InsuranceEnrollment Aggregate
 */
@Getter
public class HealthInsuranceDependent {

    /** 年齡門檻：一般子女/孫子女滿 20 歲需退保 */
    private static final int AGE_THRESHOLD_DEFAULT = 20;
    /** 年齡門檻：仍在學者可延長至 25 歲 */
    private static final int AGE_THRESHOLD_STUDENT = 25;

    private final DependentId id;
    private final String employeeId;
    private String name;
    private String idNumber;
    private DependentType type;
    private LocalDate birthDate;
    private LocalDate enrollDate;
    private LocalDate withdrawDate;
    private boolean isActive;
    /** 是否為在學延長加保（子女/孫子女仍在學者可延長至 25 歲） */
    private boolean isStudentExtension;

    public HealthInsuranceDependent(
            DependentId id,
            String employeeId,
            String name,
            String idNumber,
            DependentType type,
            LocalDate birthDate,
            LocalDate enrollDate) {

        if (id == null)
            throw new IllegalArgumentException("DependentId cannot be null");
        if (employeeId == null || employeeId.isBlank())
            throw new IllegalArgumentException("EmployeeId cannot be null or blank");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank");
        if (idNumber == null || idNumber.isBlank())
            throw new IllegalArgumentException("IdNumber cannot be null or blank");
        if (type == null)
            throw new IllegalArgumentException("DependentType cannot be null");

        this.id = id;
        this.employeeId = employeeId;
        this.name = name;
        this.idNumber = idNumber;
        this.type = type;
        this.birthDate = birthDate;
        this.enrollDate = enrollDate;
        this.isActive = true;
    }

    /**
     * 靜態工廠方法 - 建立眷屬
     */
    public static HealthInsuranceDependent create(
            String employeeId,
            String name,
            String idNumber,
            DependentType type,
            LocalDate birthDate,
            LocalDate enrollDate) {

        return new HealthInsuranceDependent(
                DependentId.generate(),
                employeeId,
                name,
                idNumber,
                type,
                birthDate,
                enrollDate);
    }

    /**
     * 靜態工廠方法 - 建立眷屬（含在學延長標記）
     */
    public static HealthInsuranceDependent create(
            String employeeId,
            String name,
            String idNumber,
            DependentType type,
            LocalDate birthDate,
            LocalDate enrollDate,
            boolean isStudentExtension) {

        HealthInsuranceDependent dependent = new HealthInsuranceDependent(
                DependentId.generate(),
                employeeId,
                name,
                idNumber,
                type,
                birthDate,
                enrollDate);
        dependent.isStudentExtension = isStudentExtension;
        return dependent;
    }

    /**
     * 退保
     */
    public void withdraw(LocalDate withdrawDate) {
        if (!isActive) {
            throw new IllegalStateException("眷屬已退保");
        }
        this.withdrawDate = withdrawDate;
        this.isActive = false;
    }

    /**
     * 更新資料
     */
    public void updateInfo(String name, DependentType type, LocalDate birthDate) {
        this.name = name;
        this.type = type;
        this.birthDate = birthDate;
    }

    /**
     * 更新在學延長標記
     */
    public void setStudentExtension(boolean isStudentExtension) {
        this.isStudentExtension = isStudentExtension;
    }

    /**
     * 檢查是否超過年齡門檻需退保
     * [2026-03-17 更新]
     * - 適用對象：CHILD（子女）、GRANDCHILD（孫子女）
     * - 一般門檻：滿 20 歲需退保
     * - 在學延長：isStudentExtension=true 時，門檻延長至 25 歲
     */
    public boolean needsAgeValidation() {
        if ((type != DependentType.CHILD && type != DependentType.GRANDCHILD) || birthDate == null) {
            return false;
        }
        int ageThreshold = isStudentExtension ? AGE_THRESHOLD_STUDENT : AGE_THRESHOLD_DEFAULT;
        LocalDate threshold = birthDate.plusYears(ageThreshold);
        return LocalDate.now().isAfter(threshold);
    }
}
