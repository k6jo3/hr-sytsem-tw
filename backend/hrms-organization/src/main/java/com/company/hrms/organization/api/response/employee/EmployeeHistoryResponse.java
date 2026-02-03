package com.company.hrms.organization.api.response.employee;

import java.time.LocalDate;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * 員工人事歷程回應 DTO
 */
@Data
@Builder
public class EmployeeHistoryResponse {
    /** 歷程 ID */
    private String id;
    /** 員工 ID */
    private String employeeId;
    /** 事件類型代碼 */
    private String eventType;
    /** 事件類型顯示名稱 */
    private String eventTypeDisplayName;
    /** 生效日期 */
    private LocalDate effectiveDate;
    /** 異動原因 */
    private String reason;
    /** 變更前資料 */
    private Map<String, Object> oldValue;
    /** 變更後資料 */
    private Map<String, Object> newValue;
}
