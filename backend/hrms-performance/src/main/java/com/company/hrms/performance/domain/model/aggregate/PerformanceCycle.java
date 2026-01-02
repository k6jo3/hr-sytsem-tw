package com.company.hrms.performance.domain.model.aggregate;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.performance.domain.model.valueobject.CycleId;
import com.company.hrms.performance.domain.model.valueobject.CycleStatus;
import com.company.hrms.performance.domain.model.valueobject.CycleType;
import com.company.hrms.performance.domain.model.valueobject.EvaluationTemplate;

/**
 * 考核週期聚合根
 */
public class PerformanceCycle {
    /**
     * 週期 ID
     */
    private CycleId cycleId;

    /**
     * 週期名稱
     */
    private String cycleName;

    /**
     * 考核類型
     */
    private CycleType cycleType;

    /**
     * 考核期間開始日
     */
    private LocalDate startDate;

    /**
     * 考核期間結束日
     */
    private LocalDate endDate;

    /**
     * 自評截止日
     */
    private LocalDate selfEvalDeadline;

    /**
     * 主管評截止日
     */
    private LocalDate managerEvalDeadline;

    /**
     * 週期狀態
     */
    private CycleStatus status;

    /**
     * 考核表單範本
     */
    private EvaluationTemplate template;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    /**
     * 建立考核週期
     */
    public static PerformanceCycle create(
            String cycleName,
            CycleType cycleType,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate selfEvalDeadline,
            LocalDate managerEvalDeadline) {

        // 驗證
        validateCycleName(cycleName);
        validateCycleType(cycleType);
        validateDateRange(startDate, endDate);
        validateDeadlines(endDate, selfEvalDeadline, managerEvalDeadline);

        PerformanceCycle cycle = new PerformanceCycle();
        cycle.cycleId = CycleId.create();
        cycle.cycleName = cycleName;
        cycle.cycleType = cycleType;
        cycle.startDate = startDate;
        cycle.endDate = endDate;
        cycle.selfEvalDeadline = selfEvalDeadline;
        cycle.managerEvalDeadline = managerEvalDeadline;
        cycle.status = CycleStatus.DRAFT;
        cycle.template = null; // 表單稍後設定
        cycle.createdAt = LocalDateTime.now();
        cycle.updatedAt = LocalDateTime.now();

        return cycle;
    }

    /**
     * 重建考核週期（用於從資料庫載入）
     */
    public static PerformanceCycle reconstitute(
            CycleId cycleId,
            String cycleName,
            CycleType cycleType,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate selfEvalDeadline,
            LocalDate managerEvalDeadline,
            CycleStatus status,
            EvaluationTemplate template,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        PerformanceCycle cycle = new PerformanceCycle();
        cycle.cycleId = cycleId;
        cycle.cycleName = cycleName;
        cycle.cycleType = cycleType;
        cycle.startDate = startDate;
        cycle.endDate = endDate;
        cycle.selfEvalDeadline = selfEvalDeadline;
        cycle.managerEvalDeadline = managerEvalDeadline;
        cycle.status = status;
        cycle.template = template;
        cycle.createdAt = createdAt;
        cycle.updatedAt = updatedAt;

        return cycle;
    }

    /**
     * 啟動考核週期
     */
    public void start() {
        if (status != CycleStatus.DRAFT) {
            throw new IllegalStateException("只有草稿狀態的週期可以啟動");
        }
        if (template == null || !template.getIsPublished()) {
            throw new IllegalStateException("表單尚未發布，無法啟動週期");
        }

        this.status = CycleStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 完成考核週期
     */
    public void complete() {
        if (status != CycleStatus.IN_PROGRESS) {
            throw new IllegalStateException("只有進行中的週期可以完成");
        }

        this.status = CycleStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新週期名稱
     */
    public void updateCycleName(String newName) {
        if (status != CycleStatus.DRAFT) {
            throw new IllegalStateException("只有草稿狀態的週期可以修改");
        }
        validateCycleName(newName);

        this.cycleName = newName;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 更新考核期間
     */
    public void updatePeriod(LocalDate newStartDate, LocalDate newEndDate) {
        if (status != CycleStatus.DRAFT) {
            throw new IllegalStateException("只有草稿狀態的週期可以修改");
        }
        validateDateRange(newStartDate, newEndDate);

        this.startDate = newStartDate;
        this.endDate = newEndDate;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 儲存考核表單範本
     */
    public void saveTemplate(EvaluationTemplate template) {
        if (status != CycleStatus.DRAFT) {
            throw new IllegalStateException("只有草稿狀態的週期可以修改表單");
        }
        if (template == null) {
            throw new IllegalArgumentException("表單範本不可為 null");
        }

        this.template = template;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 發布考核表單
     */
    public void publishTemplate() {
        if (status != CycleStatus.DRAFT) {
            throw new IllegalStateException("只有草稿狀態的週期可以發布表單");
        }
        if (template == null) {
            throw new IllegalStateException("尚未設定表單範本");
        }

        template.publish();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 檢查是否可以刪除
     */
    public boolean canDelete() {
        return status == CycleStatus.DRAFT;
    }

    // === Getters (Manual due to Lombok issues) ===

    public CycleId getCycleId() {
        return cycleId;
    }

    public String getCycleName() {
        return cycleName;
    }

    public CycleType getCycleType() {
        return cycleType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getSelfEvalDeadline() {
        return selfEvalDeadline;
    }

    public LocalDate getManagerEvalDeadline() {
        return managerEvalDeadline;
    }

    public CycleStatus getStatus() {
        return status;
    }

    public EvaluationTemplate getTemplate() {
        return template;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // === 驗證方法 ===

    private static void validateCycleName(String cycleName) {
        if (cycleName == null || cycleName.isBlank()) {
            throw new IllegalArgumentException("週期名稱不可為空");
        }
        if (cycleName.length() > 100) {
            throw new IllegalArgumentException("週期名稱不可超過 100 字元");
        }
    }

    private static void validateCycleType(CycleType cycleType) {
        if (cycleType == null) {
            throw new IllegalArgumentException("考核類型不可為空");
        }
    }

    private static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("考核期間不可為空");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("結束日期必須晚於開始日期");
        }
    }

    private static void validateDeadlines(
            LocalDate endDate,
            LocalDate selfEvalDeadline,
            LocalDate managerEvalDeadline) {

        if (selfEvalDeadline != null && selfEvalDeadline.isBefore(endDate)) {
            throw new IllegalArgumentException("自評截止日必須在考核期間結束之後");
        }
        if (managerEvalDeadline != null && selfEvalDeadline != null
                && managerEvalDeadline.isBefore(selfEvalDeadline)) {
            throw new IllegalArgumentException("主管評截止日必須在自評截止日之後");
        }
    }
}
