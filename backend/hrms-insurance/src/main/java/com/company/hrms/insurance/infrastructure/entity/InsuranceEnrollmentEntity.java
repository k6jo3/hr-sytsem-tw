package com.company.hrms.insurance.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加退保記錄JPA Entity
 */
@Entity
@Table(name = "insurance_enrollments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceEnrollmentEntity {

    @Id
    @Column(name = "enrollment_id")
    private UUID enrollmentId;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "insurance_unit_id", nullable = false)
    private UUID insuranceUnitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "insurance_type", nullable = false, length = 20)
    private InsuranceType insuranceType;

    @Column(name = "enroll_date", nullable = false)
    private LocalDate enrollDate;

    @Column(name = "withdraw_date")
    private LocalDate withdrawDate;

    @Column(name = "insurance_level_id")
    private UUID insuranceLevelId;

    @Column(name = "monthly_salary", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlySalary;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "is_reported")
    private Boolean isReported;

    @Column(name = "reported_at")
    private LocalDateTime reportedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isReported == null) {
            isReported = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
