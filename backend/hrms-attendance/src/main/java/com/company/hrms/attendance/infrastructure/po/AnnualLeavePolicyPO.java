package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AnnualLeavePolicyPO {
    private String id;
    private String name;
    private Boolean active;
    private LocalDateTime effectiveDate;

    /** 年度制度：CALENDAR_YEAR / ANNIVERSARY */
    private String annualLeaveSystem;

    /** 超額請假政策：DENY / CONVERT_TO_PERSONAL / ADVANCE */
    private String overdrawPolicy;

    /** 未休假處理政策：CARRYOVER / PAY_COMPENSATION / FORFEIT */
    private String expiryPolicy;

    /** 結轉上限天數 */
    private Integer carryOverLimit;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
