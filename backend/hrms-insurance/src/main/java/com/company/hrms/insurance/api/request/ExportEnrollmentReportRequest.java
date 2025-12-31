package com.company.hrms.insurance.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 匯出加退保申報檔請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportEnrollmentReportRequest {

    /** 匯出類型 (LABOR, HEALTH, ALL) */
    private String exportType;

    /** 起始日期 (格式: yyyy-MM-dd) */
    private String startDate;

    /** 結束日期 (格式: yyyy-MM-dd) */
    private String endDate;

    /** 投保單位ID (可選) */
    private String insuranceUnitId;

    /** 申報類型 (ENROLL: 加保, WITHDRAW: 退保, ALL: 全部) */
    private String reportType;
}
