package com.company.hrms.payroll.domain.model.valueobject;

import java.util.Set;

import com.company.hrms.common.exception.DomainException;

/**
 * 薪資批次狀態列舉
 * 具備狀態機邏輯，確保狀態流轉的合法性
 * 
 * <p>
 * 狀態流轉圖：
 * </p>
 * 
 * <pre>
 * DRAFT → CALCULATING → COMPLETED → SUBMITTED → APPROVED → PAID
 *                ↓            ↓           ↓
 *             FAILED      CANCELLED   CANCELLED
 * </pre>
 */
public enum PayrollRunStatus {

    /**
     * 草稿
     * 薪資批次剛建立，尚未執行計算
     */
    DRAFT(Set.of("CALCULATING", "CANCELLED")),

    /**
     * 計算中
     * 正在執行薪資計算
     */
    CALCULATING(Set.of("COMPLETED", "FAILED")),

    /**
     * 已完成
     * 薪資計算完成，等待送審
     */
    COMPLETED(Set.of("SUBMITTED", "CANCELLED")),

    /**
     * 計算失敗
     * 薪資計算過程發生錯誤
     */
    FAILED(Set.of("DRAFT")),

    /**
     * 已送審
     * 薪資批次已送交主管核准
     */
    SUBMITTED(Set.of("APPROVED", "COMPLETED", "CANCELLED")),

    /**
     * 已核准
     * 薪資批次已獲得核准
     */
    APPROVED(Set.of("PAID", "CANCELLED")),

    /**
     * 已發放
     * 薪資已發放 (終態)
     */
    PAID(Set.of()),

    /**
     * 已取消
     * 薪資批次已取消 (終態)
     */
    CANCELLED(Set.of());

    private final Set<String> allowedTransitions;

    PayrollRunStatus(Set<String> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    /**
     * 檢查是否可以轉換到目標狀態
     * 
     * @param targetStatus 目標狀態
     * @return 是否允許轉換
     */
    public boolean canTransitionTo(PayrollRunStatus targetStatus) {
        return allowedTransitions.contains(targetStatus.name());
    }

    /**
     * 驗證狀態轉換的合法性
     * 
     * @param targetStatus 目標狀態
     * @throws DomainException 當狀態轉換不合法時拋出
     */
    public void validateTransition(PayrollRunStatus targetStatus) {
        if (!canTransitionTo(targetStatus)) {
            throw new DomainException(
                    "PAYROLL_RUN_INVALID_TRANSITION",
                    String.format("無法從 %s 轉換到 %s", this.name(), targetStatus.name()));
        }
    }

    /**
     * 檢查是否為終態 (不可再變更)
     * 
     * @return 是否為終態
     */
    public boolean isFinal() {
        return allowedTransitions.isEmpty();
    }

    /**
     * 檢查是否可以執行薪資計算
     * 
     * @return 是否可以執行計算
     */
    public boolean canExecute() {
        return this == DRAFT;
    }

    /**
     * 檢查是否可以送審
     * 
     * @return 是否可以送審
     */
    public boolean canSubmit() {
        return this == COMPLETED;
    }

    /**
     * 檢查是否可以核准
     * 
     * @return 是否可以核准
     */
    public boolean canApprove() {
        return this == SUBMITTED;
    }
}
