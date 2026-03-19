package com.company.hrms.payroll.infrastructure.event;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 請假薪資事件處理器預設實作
 *
 * <p>目前僅記錄日誌，待後續整合薪資計算邏輯時實作完整的扣款記錄。
 * 僅在 Kafka 啟用時生效（與 {@link LeaveApprovedEventListener} 同步載入）。
 *
 * TODO: 整合 PayrollAdjustment 或 LeaveDeductionCalculator，
 *       將請假資訊寫入待計算薪資扣款表。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class DefaultLeavePayrollEventHandler
        implements LeaveApprovedEventListener.LeavePayrollEventHandler {

    @Override
    public void recordLeaveDeduction(String employeeId, String applicationId,
                                     String leaveType, BigDecimal leaveDays, BigDecimal leaveHours) {
        log.info("[LeavePayrollHandler] 記錄請假扣款 — 員工={}, 單號={}, 類型={}, 天數={}, 時數={}",
                employeeId, applicationId, leaveType, leaveDays, leaveHours);

        // TODO: 實作完整邏輯，例如：
        // 1. 根據 leaveType 判斷是否需要扣薪（特休不扣、事假扣全薪、病假扣半薪）
        // 2. 建立 PayrollAdjustment 或寫入待計算清單
        // 3. 發布 Domain Event 通知薪資計算引擎
    }
}
