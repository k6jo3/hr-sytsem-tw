package com.company.hrms.insurance.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 加退保紀錄查詢請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetEnrollmentListRequest {

    /** 員工編號 */
    private String employeeId;

    /** 保險類型 (LABOR/HEALTH/PENSION) */
    private String insuranceType;

    /** 狀態 (ACTIVE/TERMINATED/WITHDRAWN) */
    private String status;

    /** 加保日期 */
    private String enrollDate;

    /** 投保級距 */
    private String salaryGrade;

    /** 投保單位 */
    private String insuranceUnit;

    /** 是否有眷屬 */
    private Boolean hasDependents;

    /** 當前使用者 ID (用於個人查詢) */
    private String currentUserId;
}
