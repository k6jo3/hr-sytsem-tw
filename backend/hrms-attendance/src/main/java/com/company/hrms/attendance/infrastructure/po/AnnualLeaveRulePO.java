package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AnnualLeaveRulePO {
    private String id;
    private String policyId;
    private Integer minServiceYears;
    private Integer maxServiceYears;
    private Integer days;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
