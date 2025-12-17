package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 工作經歷持久化對象
 */
@Data
public class WorkExperiencePO {

    private UUID experienceId;
    private UUID employeeId;
    private String company;
    private String jobTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
