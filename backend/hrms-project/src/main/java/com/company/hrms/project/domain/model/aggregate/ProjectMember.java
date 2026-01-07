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
    private LocalDate joinDate;
    private LocalDate leaveDate;

    // Domain Constructor
    private ProjectMember(ProjectId projectId, UUID employeeId, String role, BigDecimal allocatedHours,
            LocalDate joinDate) {
        this.id = UUID.randomUUID();
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.role = role;
        this.allocatedHours = allocatedHours;
        this.joinDate = joinDate;
    }

    private ProjectMember(UUID id, ProjectId projectId, UUID employeeId, String role, BigDecimal allocatedHours,
            LocalDate joinDate, LocalDate leaveDate) {
        this.id = id;
        this.projectId = projectId;
        this.employeeId = employeeId;
        this.role = role;
        this.allocatedHours = allocatedHours;
        this.joinDate = joinDate;
        this.leaveDate = leaveDate;
    }

    public static ProjectMember create(ProjectId projectId, UUID employeeId, String role, BigDecimal allocatedHours,
            LocalDate joinDate) {
        return new ProjectMember(projectId, employeeId, role, allocatedHours, joinDate);
    }

    public static ProjectMember reconstitute(UUID id, ProjectId projectId, UUID employeeId, String role,
            BigDecimal allocatedHours,
            LocalDate joinDate, LocalDate leaveDate) {
        return new ProjectMember(id, projectId, employeeId, role, allocatedHours, joinDate, leaveDate);
    }
}
