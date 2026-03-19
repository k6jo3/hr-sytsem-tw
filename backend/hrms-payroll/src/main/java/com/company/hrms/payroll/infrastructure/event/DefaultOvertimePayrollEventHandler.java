package com.company.hrms.payroll.infrastructure.event;

import java.math.BigDecimal;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 加班薪資事件處理器預設實作
 *
 * <p>目前僅記錄日誌，待後續整合薪資計算邏輯時實作完整的加班費記錄。
 * 僅在 Kafka 啟用時生效（與 {@link OvertimeApprovedEventListener} 同步載入）。
 *
 * TODO: 整合 OvertimePayCalculator，
 *       將加班資訊寫入待計算加班費表。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class DefaultOvertimePayrollEventHandler
        implements OvertimeApprovedEventListener.OvertimePayrollEventHandler {

    @Override
    public void recordOvertimePay(String employeeId, String applicationId,
                                  String overtimeType, BigDecimal overtimeHours) {
        log.info("[OvertimePayrollHandler] 記錄加班費 — 員工={}, 單號={}, 類型={}, 時數={}",
                employeeId, applicationId, overtimeType, overtimeHours);

        // TODO: 實作完整邏輯，例如：
        // 1. 根據 overtimeType 決定加班費率（平日 1.34x/1.67x、休息日、國定假日）
        // 2. 呼叫 OvertimePayCalculator 計算加班費
        // 3. 建立 PayrollAdjustment 或寫入待計算清單
        // 4. 發布 Domain Event 通知薪資計算引擎
    }
}
