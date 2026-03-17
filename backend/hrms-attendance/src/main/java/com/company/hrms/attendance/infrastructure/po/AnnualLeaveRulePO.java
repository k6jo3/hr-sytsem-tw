package com.company.hrms.attendance.infrastructure.po;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 特休規則持久化物件
 *
 * <p>欄位已從 minServiceYears/maxServiceYears 調整為
 * minServiceMonths/maxServiceMonths，以支援勞基法第 38 條的
 * 6 個月起算等非整數年資情境。
 */
@Data
public class AnnualLeaveRulePO {
    private String id;
    private String policyId;

    /** 最低年資月數（含） */
    private Integer minServiceMonths;

    /** 最高年資月數（不含） */
    private Integer maxServiceMonths;

    /** 對應特休天數 */
    private Integer days;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
