package com.company.hrms.attendance.infrastructure.po;

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
 * 輪班模式持久化物件
 */
@Entity
@Table(name = "rotation_patterns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RotationPatternPO {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "organization_id", length = 50, nullable = false)
    private String organizationId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "cycle_days", nullable = false)
    private Integer cycleDays;

    @Column(name = "is_active")
    private Integer isActive;

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
