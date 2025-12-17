package com.company.hrms.organization.infrastructure.po;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 學歷持久化對象
 */
@Data
public class EducationPO {

    private UUID educationId;
    private UUID employeeId;
    private String degree;
    private String school;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isHighestDegree;
}
