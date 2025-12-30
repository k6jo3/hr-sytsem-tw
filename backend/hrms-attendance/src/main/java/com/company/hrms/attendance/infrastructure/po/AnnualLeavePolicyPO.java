package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AnnualLeavePolicyPO {
    private String id;
    private String name;
    private Boolean active;
    private LocalDateTime effectiveDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
