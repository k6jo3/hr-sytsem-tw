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
    @com.company.hrms.common.query.QueryCondition.EQ("employee_id")
    private String employeeId;

    /** 保險類型 (LABOR/HEALTH/PENSION) */
    @com.company.hrms.common.query.QueryCondition.EQ("insurance_type")
    private String insuranceType;

    /** 狀態 (ACTIVE/TERMINATED/WITHDRAWN) */
    @com.company.hrms.common.query.QueryCondition.EQ("status")
    private String status;

    /** 加保日期 */
    @com.company.hrms.common.query.QueryCondition.EQ("enroll_date")
    private String enrollDate;

    /** 投保級距 */
    @com.company.hrms.common.query.QueryCondition.EQ("salary_grade")
    private String salaryGrade;

    /** 投保單位 */
    @com.company.hrms.common.query.QueryCondition.EQ("insurance_unit")
    private String insuranceUnit;

    /** 是否有眷屬 */
    @com.company.hrms.common.query.QueryCondition.EQ("has_dependents")
    private Boolean hasDependents;

    /** 當前使用者 ID (用於個人查詢) */
    // Special handling in Assembler if needed, or mapped to same field?
    // If annotated, it adds AND condition.
    // If Controller logic ensures only one is set (or they match), it's fine.
    // If not annotated, we can add manually in Assembler.
    private String currentUserId;
}
