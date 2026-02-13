package com.company.hrms.project.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.company.hrms.project.domain.model.valueobject.ProjectId;

import lombok.Getter;

@Getter
public class ProjectMember {

    private UUID id;
    private ProjectId projectId; // Add reference to ProjectId
    private UUID employeeId;
    private String role;
    private BigDecimal allocatedHours;
    private BigDecimal hourlyRate;
    private LocalDate joinDate;
    private LocalDate leaveDate;

    // Domain Constructor
    private ProjectMember(ProjectId projectId, UUID employeeId, String role, BigDecimal allocatedHours,
            BigDecimal hourlyRate, LocalDate joinDate) {
        this.id = UUID.randomUUID();
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.role = role;
        this.allocatedHours = allocatedHours;
        this.hourlyRate = hourlyRate;
        this.joinDate = joinDate;
    }

    private ProjectMember(UUID id, ProjectId projectId, UUID employeeId, String role, BigDecimal allocatedHours,
            BigDecimal hourlyRate, LocalDate joinDate, LocalDate leaveDate) {
        this.id = id;
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.role = role;
        this.allocatedHours = allocatedHours;
        this.hourlyRate = hourlyRate;
        this.joinDate = joinDate;
        this.leaveDate = leaveDate;
    }

    public static ProjectMember create(ProjectId projectId, UUID employeeId, String role, BigDecimal allocatedHours,
            BigDecimal hourlyRate, LocalDate joinDate) {
        return new ProjectMember(projectId, employeeId, role, allocatedHours, hourlyRate, joinDate);
    }

    public static ProjectMember reconstitute(UUID id, ProjectId projectId, UUID employeeId, String role,
            BigDecimal allocatedHours, BigDecimal hourlyRate,
            LocalDate joinDate, LocalDate leaveDate) {
        return new ProjectMember(id, projectId, employeeId, role, allocatedHours, hourlyRate, joinDate, leaveDate);
    }

    /**
     * 設定成員離開日期
     *
     * @param leaveDate 離開日期
     */
    public void setLeaveDate(LocalDate leaveDate) {
        this.leaveDate = leaveDate;
    }

    /**
     * 更新配置工時
     *
     * @param allocatedHours 配置工時
     */
    public void updateAllocatedHours(BigDecimal allocatedHours) {
        this.allocatedHours = allocatedHours;
    }

    /**
     * 更新角色
     *
     * @param role 新角色
     */
    public void updateRole(String role) {
        this.role = role;
    }
}
