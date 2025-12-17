package com.company.hrms.organization.domain.service;

import com.company.hrms.organization.domain.model.entity.EmployeeContract;
import com.company.hrms.organization.domain.model.valueobject.ContractStatus;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 合約到期檢查 Domain Service
 * 負責處理員工合約的到期檢查和提醒邏輯
 */
@Service
@RequiredArgsConstructor
public class ContractExpiryCheckDomainService {

    private final IEmployeeContractRepository contractRepository;

    /**
     * 預設提醒天數 (30天前提醒)
     */
    private static final int DEFAULT_REMINDER_DAYS = 30;

    /**
     * 緊急提醒天數 (7天前緊急提醒)
     */
    private static final int URGENT_REMINDER_DAYS = 7;

    /**
     * 合約到期檢查結果
     */
    public static class ExpiryCheckResult {
        private final EmployeeContract contract;
        private final long daysUntilExpiry;
        private final ExpiryLevel level;

        public ExpiryCheckResult(EmployeeContract contract, long daysUntilExpiry, ExpiryLevel level) {
            this.contract = contract;
            this.daysUntilExpiry = daysUntilExpiry;
            this.level = level;
        }

        public EmployeeContract getContract() {
            return contract;
        }

        public long getDaysUntilExpiry() {
            return daysUntilExpiry;
        }

        public ExpiryLevel getLevel() {
            return level;
        }
    }

    /**
     * 到期等級
     */
    public enum ExpiryLevel {
        EXPIRED,    // 已過期
        URGENT,     // 緊急 (7天內)
        WARNING,    // 警告 (30天內)
        NORMAL      // 正常
    }

    /**
     * 檢查員工的合約狀態
     * @param employeeId 員工ID
     * @return 到期檢查結果 (如果有有效合約)
     */
    public Optional<ExpiryCheckResult> checkEmployeeContract(EmployeeId employeeId) {
        Optional<EmployeeContract> activeContract = contractRepository.findActiveByEmployeeId(employeeId);

        return activeContract.map(this::checkContractExpiry);
    }

    /**
     * 檢查單一合約的到期狀態
     * @param contract 合約
     * @return 到期檢查結果
     */
    public ExpiryCheckResult checkContractExpiry(EmployeeContract contract) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = contract.getEndDate();

        if (endDate == null) {
            // 無固定期限合約
            return new ExpiryCheckResult(contract, Long.MAX_VALUE, ExpiryLevel.NORMAL);
        }

        long daysUntilExpiry = ChronoUnit.DAYS.between(today, endDate);
        ExpiryLevel level = determineExpiryLevel(daysUntilExpiry);

        return new ExpiryCheckResult(contract, daysUntilExpiry, level);
    }

    /**
     * 取得即將到期的合約列表
     * @param withinDays 幾天內到期
     * @return 即將到期的合約列表
     */
    public List<EmployeeContract> getExpiringContracts(int withinDays) {
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = today.plusDays(withinDays);

        return contractRepository.findExpiringBefore(expiryDate).stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    /**
     * 取得需要提醒的合約列表 (30天內到期)
     * @return 需要提醒的合約列表
     */
    public List<ExpiryCheckResult> getContractsNeedingReminder() {
        LocalDate today = LocalDate.now();
        LocalDate reminderDate = today.plusDays(DEFAULT_REMINDER_DAYS);

        return contractRepository.findExpiringBefore(reminderDate).stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                .map(this::checkContractExpiry)
                .filter(r -> r.getLevel() != ExpiryLevel.NORMAL)
                .collect(Collectors.toList());
    }

    /**
     * 取得緊急需要處理的合約列表 (7天內到期或已過期)
     * @return 緊急合約列表
     */
    public List<ExpiryCheckResult> getUrgentContracts() {
        LocalDate today = LocalDate.now();
        LocalDate urgentDate = today.plusDays(URGENT_REMINDER_DAYS);

        return contractRepository.findExpiringBefore(urgentDate).stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                .map(this::checkContractExpiry)
                .filter(r -> r.getLevel() == ExpiryLevel.URGENT || r.getLevel() == ExpiryLevel.EXPIRED)
                .collect(Collectors.toList());
    }

    /**
     * 取得已過期但未處理的合約
     * @return 已過期合約列表
     */
    public List<EmployeeContract> getExpiredContracts() {
        LocalDate today = LocalDate.now();

        return contractRepository.findExpiringBefore(today).stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                .filter(c -> c.getEndDate() != null && c.getEndDate().isBefore(today))
                .collect(Collectors.toList());
    }

    /**
     * 檢查是否可以續約
     * @param contract 合約
     * @return 是否可以續約
     */
    public boolean canRenew(EmployeeContract contract) {
        // 只有有效合約可以續約
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            return false;
        }

        // 無固定期限合約不需要續約
        if (contract.getEndDate() == null) {
            return false;
        }

        // 合約到期前90天內可以續約
        LocalDate today = LocalDate.now();
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, contract.getEndDate());

        return daysUntilExpiry <= 90;
    }

    /**
     * 計算建議的續約開始日期
     * @param currentContract 目前合約
     * @return 建議的續約開始日期
     */
    public LocalDate calculateRenewalStartDate(EmployeeContract currentContract) {
        if (currentContract.getEndDate() == null) {
            return null;
        }

        // 續約從目前合約結束日期的隔天開始
        return currentContract.getEndDate().plusDays(1);
    }

    /**
     * 決定到期等級
     * @param daysUntilExpiry 距離到期天數
     * @return 到期等級
     */
    private ExpiryLevel determineExpiryLevel(long daysUntilExpiry) {
        if (daysUntilExpiry < 0) {
            return ExpiryLevel.EXPIRED;
        } else if (daysUntilExpiry <= URGENT_REMINDER_DAYS) {
            return ExpiryLevel.URGENT;
        } else if (daysUntilExpiry <= DEFAULT_REMINDER_DAYS) {
            return ExpiryLevel.WARNING;
        } else {
            return ExpiryLevel.NORMAL;
        }
    }

    /**
     * 取得預設提醒天數
     * @return 預設提醒天數
     */
    public int getDefaultReminderDays() {
        return DEFAULT_REMINDER_DAYS;
    }

    /**
     * 取得緊急提醒天數
     * @return 緊急提醒天數
     */
    public int getUrgentReminderDays() {
        return URGENT_REMINDER_DAYS;
    }
}
