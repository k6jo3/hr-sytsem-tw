package com.company.hrms.attendance.domain.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 假期額度初始化 Domain Service
 *
 * <p>負責在員工到職時初始化年度假期額度（特休、事假、病假等）。
 * 由 {@link com.company.hrms.attendance.infrastructure.event.EmployeeCreatedEventListener}
 * 在收到員工建立事件時呼叫。
 *
 * <p>初始化邏輯應依據：
 * <ul>
 *   <li>企業自訂的 AnnualLeavePolicy</li>
 *   <li>勞基法法定特休基準（搭配 {@link StatutoryAnnualLeaveCalculator}）</li>
 *   <li>到職日期計算比例制年假（未滿一年按比例）</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveInitializationDomainService {

    // TODO: 注入 ILeaveBalanceRepository 以持久化初始假期額度
    // TODO: 注入 AnnualLeaveCalculationDomainService 以計算特休天數

    /**
     * 為新進員工初始化年度假期額度
     *
     * <p>預期行為：
     * <ol>
     *   <li>依到職日期計算當年度特休天數（新進員工通常為 0 或按比例）</li>
     *   <li>初始化法定假別額度（事假 14 天、病假 30 天等）</li>
     *   <li>建立 LeaveBalance 聚合根並持久化</li>
     * </ol>
     *
     * @param employeeId 員工 ID
     * @param hireDate   到職日期（ISO 格式字串，如 "2026-03-19"），可能為 null
     */
    public void initializeAnnualLeaveForNewEmployee(String employeeId, String hireDate) {
        log.info("[LeaveInitialization] 初始化員工年度假期額度 - employeeId={}, hireDate={}", employeeId, hireDate);

        // TODO: 實作完整的假期額度初始化邏輯
        // 1. 解析 hireDate，計算到職年度
        // 2. 查詢企業 AnnualLeavePolicy
        // 3. 計算特休天數（新進員工第一年依比例）
        // 4. 建立各假別的 LeaveBalance（特休、事假、病假、婚假、喪假等）
        // 5. 透過 ILeaveBalanceRepository 持久化

        log.info("[LeaveInitialization] 員工假期額度初始化完成（目前為佔位實作） - employeeId={}", employeeId);
    }
}
