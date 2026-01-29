package com.company.hrms.reporting.infrastructure.readmodel;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 差勤統計讀模型
 * 
 * <p>
 * 從考勤服務的事件更新
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Entity
@Table(name = "rm_attendance_statistics", indexes = {
        @Index(name = "idx_tenant_date", columnList = "tenant_id,stat_date"),
        @Index(name = "idx_employee_date", columnList = "employee_id,stat_date"),
        @Index(name = "idx_department_date", columnList = "department_id,stat_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceStatisticsReadModel {

    @Id
    @Column(name = "id", length = 100)
    private String id; // employeeId + statDate

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "employee_id", length = 50, nullable = false)
    private String employeeId;

    @Column(name = "employee_name", length = 100)
    private String employeeName;

    @Column(name = "department_id", length = 50)
    private String departmentId;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "stat_date")
    private LocalDate statDate;

    @Column(name = "expected_days")
    private Integer expectedDays;

    @Column(name = "actual_days")
    private Integer actualDays;

    @Column(name = "late_count")
    private Integer lateCount;

    @Column(name = "early_leave_count")
    private Integer earlyLeaveCount;

    @Column(name = "absent_count")
    private Integer absentCount;

    @Column(name = "leave_days", precision = 10, scale = 2)
    private Double leaveDays;

    @Column(name = "overtime_hours", precision = 10, scale = 2)
    private Double overtimeHours;

    @Column(name = "attendance_rate", precision = 5, scale = 2)
    private Double attendanceRate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
