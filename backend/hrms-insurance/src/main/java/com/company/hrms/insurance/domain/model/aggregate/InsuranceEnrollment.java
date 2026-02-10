package com.company.hrms.insurance.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.model.valueobject.UnitId;

/**
 * 加退保記錄聚合根
 */
public class InsuranceEnrollment {
    private final EnrollmentId id;
    private final String employeeId;
    private final UnitId insuranceUnitId;
    private final InsuranceType insuranceType;
    private final LocalDate enrollDate;
    private LocalDate withdrawDate;
    private LevelId insuranceLevelId;
    private BigDecimal monthlySalary;
    private EnrollmentStatus status;
    private boolean isReported;

    /**
     * 建立加保記錄
     */
    /**
     * 基本建構子 (用於新建)
     */
    public InsuranceEnrollment(
            EnrollmentId id,
            String employeeId,
            UnitId unitId,
            InsuranceType type,
            LocalDate enrollDate,
            LevelId levelId,
            BigDecimal monthlySalary) {
        this(id, employeeId, unitId, type, enrollDate, null, levelId, monthlySalary, EnrollmentStatus.ACTIVE, false);
    }

    /**
     * 全參數建構子 (用於 Repository 重建)
     */
    public InsuranceEnrollment(
            EnrollmentId id,
            String employeeId,
            UnitId unitId,
            InsuranceType type,
            LocalDate enrollDate,
            LocalDate withdrawDate,
            LevelId levelId,
            BigDecimal monthlySalary,
            EnrollmentStatus status,
            boolean isReported) {

        this.id = id;
        this.employeeId = employeeId;
        this.insuranceUnitId = unitId;
        this.insuranceType = type;
        this.enrollDate = enrollDate;
        this.withdrawDate = withdrawDate;
        this.insuranceLevelId = levelId;
        this.monthlySalary = monthlySalary;
        this.status = status;
        this.isReported = isReported;
    }

    /**
     * 工廠方法：建立加保記錄
     */
    public static InsuranceEnrollment enroll(
            String employeeId,
            UnitId unitId,
            InsuranceType type,
            InsuranceLevel level,
            LocalDate enrollDate) {

        return new InsuranceEnrollment(
                EnrollmentId.generate(),
                employeeId,
                unitId,
                type,
                enrollDate,
                level.getId(),
                level.getMonthlySalary());
    }

    /**
     * 退保
     */
    public void withdraw(LocalDate withdrawDate) {
        if (this.status != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("只有已加保狀態可以退保");
        }
        if (withdrawDate.isBefore(this.enrollDate)) {
            throw new IllegalArgumentException("退保日期不可早於加保日期");
        }
        this.withdrawDate = withdrawDate;
        this.status = EnrollmentStatus.WITHDRAWN;
    }

    /**
     * 調整投保級距
     */
    public void adjustLevel(InsuranceLevel newLevel) {
        if (this.status != EnrollmentStatus.ACTIVE) {
            throw new IllegalStateException("只有已加保狀態可以調整級距");
        }
        this.insuranceLevelId = newLevel.getId();
        this.monthlySalary = newLevel.getMonthlySalary();
    }

    /**
     * 標記為已申報
     */
    public void markAsReported() {
        this.isReported = true;
    }

    // Getters
    public EnrollmentId getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public UnitId getInsuranceUnitId() {
        return insuranceUnitId;
    }

    public InsuranceType getInsuranceType() {
        return insuranceType;
    }

    public LocalDate getEnrollDate() {
        return enrollDate;
    }

    public LocalDate getWithdrawDate() {
        return withdrawDate;
    }

    public LevelId getInsuranceLevelId() {
        return insuranceLevelId;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public boolean isReported() {
        return isReported;
    }
}
