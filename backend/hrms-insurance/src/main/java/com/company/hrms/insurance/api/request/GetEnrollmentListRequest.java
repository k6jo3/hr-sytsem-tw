package com.company.hrms.insurance.api.request;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

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
    @QueryFilter(property = "employee_id", operator = Operator.EQ)
    private String employeeId;

    /** 保險類型 (LABOR/HEALTH/PENSION) */
    @QueryFilter(property = "insurance_type", operator = Operator.EQ)
    private String insuranceType;

    /** 狀態 (ACTIVE/TERMINATED/WITHDRAWN) */
    @QueryFilter(property = "status", operator = Operator.EQ)
    private String status;

    /** 加保日期 */
    @QueryFilter(property = "enroll_date", operator = Operator.EQ)
    private String enrollDate;

    /** 投保級距 */
    @QueryFilter(property = "salary_grade", operator = Operator.EQ)
    private String salaryGrade;

    /** 投保單位 */
    @QueryFilter(property = "insurance_unit", operator = Operator.EQ)
    private String insuranceUnit;

    /** 是否有眷屬 */
    @QueryFilter(property = "has_dependents", operator = Operator.EQ)
    private Boolean hasDependents;

    /** 年月 (格式: YYYY-MM) */
    @QueryFilter(property = "year_month", operator = Operator.EQ)
    private String yearMonth;

    /** 提撥率 */
    @QueryFilter(property = "contribution_rate", operator = Operator.EQ)
    private String contributionRate;

    /** 是否有自提 (傳入true時，查詢 voluntary_rate > 0) */
    // 特殊處理：需在 Assembler 中手動處理
    private Boolean hasVoluntary;

    /** 眷屬關係 */
    @QueryFilter(property = "relationship", operator = Operator.EQ)
    private String relationship;

    /** 職災發生日期 */
    @QueryFilter(property = "incident_date", operator = Operator.EQ)
    private String incidentDate;

    /** 當前使用者 ID (用於個人查詢) */
    // 特殊處理：需在 Assembler 中手動處理
    private String currentUserId;
}
