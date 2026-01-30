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
 * 班別持久化物件 (Persistence Object)
 * 兼作 JPA Entity 以支援 Fluent Query Engine
 */
@Entity
@Table(name = "shifts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftPO {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "organization_id", length = 50)
    private String organizationId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "type", length = 50, nullable = false)
    private String type;

    @Column(name = "start_time", length = 20, nullable = false)
    private String startTime; // HH:mm:ss

    @Column(name = "end_time", length = 20, nullable = false)
    private String endTime; // HH:mm:ss

    @Column(name = "break_start_time", length = 20)
    private String breakStartTime; // HH:mm:ss

    @Column(name = "break_end_time", length = 20)
    private String breakEndTime; // HH:mm:ss

    @Column(name = "late_tolerance_minutes")
    private Integer lateToleranceMinutes;

    @Column(name = "early_leave_tolerance_minutes")
    private Integer earlyLeaveToleranceMinutes;

    @Column(name = "is_active")
    private Integer isActive; // 使用 Integer 配合原有邏輯或 Boolean 都行，此處改為符合 Assembler 邏輯

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
