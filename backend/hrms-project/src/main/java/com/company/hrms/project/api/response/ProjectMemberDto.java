package com.company.hrms.project.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDto {
    private String id;
    private UUID employeeId;
    private String role;
    private BigDecimal allocatedHours;
    private LocalDate joinDate;
    private LocalDate leaveDate;
}
