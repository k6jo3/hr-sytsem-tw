package com.company.hrms.attendance.infrastructure.po;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 假別持久化物件 (Persistence Object)
 * 兼作 JPA Entity 以支援 Fluent Query Engine (Querydsl)
 */
@Entity
@Table(name = "leave_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypePO {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "organization_id", length = 50)
    private String organizationId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "unit", length = 20, nullable = false)
    private String unit;

    @Column(name = "is_paid")
    private Boolean isPaid;

    @Column(name = "pay_rate", precision = 5, scale = 2)
    private BigDecimal payRate;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_statutory_leave")
    private Boolean isStatutoryLeave;

    @Column(name = "statutory_type", length = 50)
    private String statutoryType;

    @Column(name = "requires_proof")
    private Boolean requiresProof;

    @Column(name = "proof_description", length = 500)
    private String proofDescription;

    @Column(name = "max_days_per_year", precision = 5, scale = 2)
    private BigDecimal maxDaysPerYear;

    @Column(name = "can_carryover")
    private Boolean canCarryover;

    @Column(name = "is_deleted")
    private Integer isDeleted;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
