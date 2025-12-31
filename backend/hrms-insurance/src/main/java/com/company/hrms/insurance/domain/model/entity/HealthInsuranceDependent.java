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

    private final DependentId id;
    private final String employeeId;
    private String name;
    private String idNumber;
    private DependentType type;
    private LocalDate birthDate;
    private LocalDate enrollDate;
    private LocalDate withdrawDate;
    private boolean isActive;

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
     * 檢查是否需要驗證年齡 (子女滿23歲需退保)
     */
    public boolean needsAgeValidation() {
        if (type != DependentType.CHILD || birthDate == null) {
            return false;
        }
        LocalDate threshold = birthDate.plusYears(23);
        return LocalDate.now().isAfter(threshold);
    }
}
