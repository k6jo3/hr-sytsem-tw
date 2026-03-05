package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDate;
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
 * 排班表持久化物件
 */
@Entity
@Table(name = "shift_schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftSchedulePO {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "shift_id", length = 50, nullable = false)
    private String shiftId;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "rotation_pattern_id", length = 50)
    private String rotationPatternId;

    @Column(name = "note", length = 500)
    private String note;

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
