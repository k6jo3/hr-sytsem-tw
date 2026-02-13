package com.company.hrms.project.infrastructure.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberEntity {

    @Id
    @Column(name = "member_id")
    private UUID id;

    // projectId is handled by @JoinColumn in ProjectEntity but useful to have here?
    // If we use @JoinColumn in parent, we usually don't map it here or map as
    // insertable=false
    // However, keeping it simple as POJO for now.

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "allocated_hours")
    private BigDecimal allocatedHours;

    @Column(name = "hourly_rate")
    private BigDecimal hourlyRate;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Column(name = "leave_date")
    private LocalDate leaveDate;
}
