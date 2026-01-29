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
 * 員工花名冊讀模型
 * 
 * <p>
 * 從組織服務的事件更新
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Entity
@Table(name = "rm_employee_roster", indexes = {
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_department_id", columnList = "department_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_hire_date", columnList = "hire_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRosterReadModel {

    @Id
    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "employee_name", length = 100, nullable = false)
    private String name;

    @Column(name = "department_id", length = 50)
    private String departmentId;

    @Column(name = "department_name", length = 100)
    private String departmentName;

    @Column(name = "position_id", length = 50)
    private String positionId;

    @Column(name = "position_name", length = 100)
    private String positionName;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "service_years", precision = 10, scale = 2)
    private Double serviceYears;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
}
