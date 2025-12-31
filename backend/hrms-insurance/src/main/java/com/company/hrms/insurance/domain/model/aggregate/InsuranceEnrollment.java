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
    public InsuranceEnrollment(
            EnrollmentId id,
            String employeeId,
            UnitId unitId,
            InsuranceType type,
            LocalDate enrollDate,
            LevelId levelId,
            BigDecimal monthlySalary) {

        if (id == null)
            throw new IllegalArgumentException("EnrollmentId cannot be null");
        if (employeeId == null || employeeId.isBlank())
            throw new IllegalArgumentException("EmployeeId cannot be null or blank");
        if (unitId == null)
            throw new IllegalArgumentException("UnitId cannot be null");
        if (type == null)
            throw new IllegalArgumentException("InsuranceType cannot be null");
        if (enrollDate == null)
            throw new IllegalArgumentException("EnrollDate cannot be null");
        if (monthlySalary == null || monthlySalary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("MonthlySalary must be positive");
        }

        this.id = id;
        this.employeeId = employeeId;
        this.insuranceUnitId = unitId;
        this.insuranceType = type;
        this.enrollDate = enrollDate;
        this.insuranceLevelId = levelId;
        this.monthlySalary = monthlySalary;
        this.status = EnrollmentStatus.ACTIVE;
        this.isReported = false;
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
