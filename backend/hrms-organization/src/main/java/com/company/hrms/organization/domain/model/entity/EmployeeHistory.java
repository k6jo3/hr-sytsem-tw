package com.company.hrms.organization.domain.model.entity;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.EmployeeHistoryEventType;
import com.company.hrms.organization.domain.model.valueobject.HistoryId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 員工人事歷程實體
 * 記錄員工所有的人事異動
 */
@Getter
@Builder
public class EmployeeHistory {

    /**
     * 歷程 ID
     */
    private final HistoryId id;

    /**
     * 員工 ID
     */
    private UUID employeeId;

    /**
     * 事件類型
     */
    private EmployeeHistoryEventType eventType;

    /**
     * 生效日期
     */
    private LocalDate effectiveDate;

    /**
     * 變更前資料 (JSON 格式)
     */
    private Map<String, Object> oldValue;

    /**
     * 變更後資料 (JSON 格式)
     */
    private Map<String, Object> newValue;

    /**
     * 異動原因
     */
    private String reason;

    /**
     * 核准者 ID
     */
    private UUID approvedBy;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    // ==================== 工廠方法 ====================

    /**
     * 記錄到職
     */
    public static EmployeeHistory recordOnboarding(UUID employeeId, LocalDate hireDate,
                                                    Map<String, Object> employeeData) {
        return create(employeeId, EmployeeHistoryEventType.ONBOARDING, hireDate,
                null, employeeData, "新進員工到職", null);
    }

    /**
     * 記錄試用期轉正
     */
    public static EmployeeHistory recordProbationPassed(UUID employeeId, LocalDate effectiveDate,
                                                         UUID approvedBy) {
        return create(employeeId, EmployeeHistoryEventType.PROBATION_PASSED, effectiveDate,
                Map.of("status", "PROBATION"),
                Map.of("status", "ACTIVE"),
                "試用期轉正", approvedBy);
    }

    /**
     * 記錄部門調動
     */
    public static EmployeeHistory recordDepartmentTransfer(UUID employeeId, LocalDate effectiveDate,
                                                            UUID oldDepartmentId, UUID newDepartmentId,
                                                            String reason, UUID approvedBy) {
        return create(employeeId, EmployeeHistoryEventType.DEPARTMENT_TRANSFER, effectiveDate,
                Map.of("departmentId", oldDepartmentId.toString()),
                Map.of("departmentId", newDepartmentId.toString()),
                reason, approvedBy);
    }

    /**
     * 記錄職務異動
     */
    public static EmployeeHistory recordJobChange(UUID employeeId, LocalDate effectiveDate,
                                                   String oldJobTitle, String newJobTitle,
                                                   String reason, UUID approvedBy) {
        return create(employeeId, EmployeeHistoryEventType.JOB_CHANGE, effectiveDate,
                Map.of("jobTitle", oldJobTitle),
                Map.of("jobTitle", newJobTitle),
                reason, approvedBy);
    }

    /**
     * 記錄升遷
     */
    public static EmployeeHistory recordPromotion(UUID employeeId, LocalDate effectiveDate,
                                                   String oldJobTitle, String oldJobLevel,
                                                   String newJobTitle, String newJobLevel,
                                                   String reason, UUID approvedBy) {
        return create(employeeId, EmployeeHistoryEventType.PROMOTION, effectiveDate,
                Map.of("jobTitle", oldJobTitle, "jobLevel", oldJobLevel),
                Map.of("jobTitle", newJobTitle, "jobLevel", newJobLevel),
                reason, approvedBy);
    }

    /**
     * 記錄調薪
     */
    public static EmployeeHistory recordSalaryAdjustment(UUID employeeId, LocalDate effectiveDate,
                                                          String reason, UUID approvedBy) {
        return create(employeeId, EmployeeHistoryEventType.SALARY_ADJUSTMENT, effectiveDate,
                null, null, reason, approvedBy);
    }

    /**
     * 記錄離職
     */
    public static EmployeeHistory recordTermination(UUID employeeId, LocalDate terminationDate,
                                                     String reason, UUID approvedBy) {
        return create(employeeId, EmployeeHistoryEventType.TERMINATION, terminationDate,
                Map.of("status", "ACTIVE"),
                Map.of("status", "TERMINATED"),
                reason, approvedBy);
    }

    /**
     * 記錄復職
     */
    public static EmployeeHistory recordRehire(UUID employeeId, LocalDate effectiveDate,
                                                String reason, UUID approvedBy) {
        return create(employeeId, EmployeeHistoryEventType.REHIRE, effectiveDate,
                Map.of("status", "TERMINATED"),
                Map.of("status", "ACTIVE"),
                reason, approvedBy);
    }

    /**
     * 通用建立方法
     */
    private static EmployeeHistory create(UUID employeeId, EmployeeHistoryEventType eventType,
                                           LocalDate effectiveDate, Map<String, Object> oldValue,
                                           Map<String, Object> newValue, String reason, UUID approvedBy) {
        if (employeeId == null) {
            throw new DomainException("EMPLOYEE_ID_REQUIRED", "員工 ID 不可為空");
        }
        if (eventType == null) {
            throw new DomainException("EVENT_TYPE_REQUIRED", "事件類型不可為空");
        }
        if (effectiveDate == null) {
            throw new DomainException("EFFECTIVE_DATE_REQUIRED", "生效日期不可為空");
        }

        return EmployeeHistory.builder()
                .id(HistoryId.generate())
                .employeeId(employeeId)
                .eventType(eventType)
                .effectiveDate(effectiveDate)
                .oldValue(oldValue)
                .newValue(newValue)
                .reason(reason)
                .approvedBy(approvedBy)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ==================== 查詢方法 ====================

    /**
     * 取得事件類型顯示名稱
     * @return 顯示名稱
     */
    public String getEventTypeDisplayName() {
        return this.eventType.getDisplayName();
    }
}
