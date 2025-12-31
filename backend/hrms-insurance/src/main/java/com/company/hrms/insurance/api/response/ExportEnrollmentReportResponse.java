package com.company.hrms.insurance.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 匯出加退保申報檔回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExportEnrollmentReportResponse {

    /** 申報類型 */
    private String reportType;

    /** 匯出類型 (LABOR, HEALTH) */
    private String exportType;

    /** 記錄筆數 */
    private int totalRecords;

    /** 檔案名稱 */
    private String fileName;

    /** 檔案內容 (Base64 編碼) */
    private String fileContent;

    /** 匯出明細 */
    private List<ExportRecord> records;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExportRecord {
        private String employeeId;
        private String employeeName;
        private String idNumber;
        private String insuranceType;
        private String actionType; // ENROLL, WITHDRAW
        private String actionDate;
        private String monthlySalary;
    }
}
