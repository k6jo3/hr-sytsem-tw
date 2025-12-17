package com.company.hrms.organization.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.ContractId;
import com.company.hrms.organization.domain.model.valueobject.ContractStatus;
import com.company.hrms.organization.domain.model.valueobject.ContractType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 員工合約聚合根
 * 管理員工的勞動合約
 */
@Getter
@Builder
public class EmployeeContract {

    /**
     * 預設每週工時
     */
    public static final BigDecimal DEFAULT_WORKING_HOURS = new BigDecimal("40.00");

    /**
     * 合約 ID
     */
    private final ContractId id;

    /**
     * 員工 ID
     */
    private UUID employeeId;

    /**
     * 合約類型 (不定期/定期)
     */
    private ContractType contractType;

    /**
     * 合約編號 (唯一)
     */
    private String contractNumber;

    /**
     * 合約開始日期
     */
    private LocalDate startDate;

    /**
     * 合約結束日期 (不定期合約為 null)
     */
    private LocalDate endDate;

    /**
     * 每週工時
     */
    private BigDecimal workingHours;

    /**
     * 試用期月數
     */
    private Integer trialPeriodMonths;

    /**
     * 附件 URL
     */
    private String attachmentUrl;

    /**
     * 合約狀態
     */
    private ContractStatus status;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立不定期合約
     * @param employeeId 員工 ID
     * @param contractNumber 合約編號
     * @param startDate 開始日期
     * @param workingHours 每週工時
     * @param trialPeriodMonths 試用期月數
     * @return 新的合約實例
     */
    public static EmployeeContract createIndefinite(
            UUID employeeId,
            String contractNumber,
            LocalDate startDate,
            BigDecimal workingHours,
            Integer trialPeriodMonths) {

        validateEmployeeId(employeeId);
        validateContractNumber(contractNumber);
        validateStartDate(startDate);

        return EmployeeContract.builder()
                .id(ContractId.generate())
                .employeeId(employeeId)
                .contractType(ContractType.INDEFINITE)
                .contractNumber(contractNumber)
                .startDate(startDate)
                .endDate(null)
                .workingHours(workingHours != null ? workingHours : DEFAULT_WORKING_HOURS)
                .trialPeriodMonths(trialPeriodMonths != null ? trialPeriodMonths : 0)
                .status(ContractStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 建立定期合約
     * @param employeeId 員工 ID
     * @param contractNumber 合約編號
     * @param startDate 開始日期
     * @param endDate 結束日期
     * @param workingHours 每週工時
     * @param trialPeriodMonths 試用期月數
     * @return 新的合約實例
     */
    public static EmployeeContract createFixedTerm(
            UUID employeeId,
            String contractNumber,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal workingHours,
            Integer trialPeriodMonths) {

        validateEmployeeId(employeeId);
        validateContractNumber(contractNumber);
        validateStartDate(startDate);
        validateEndDate(startDate, endDate);

        return EmployeeContract.builder()
                .id(ContractId.generate())
                .employeeId(employeeId)
                .contractType(ContractType.FIXED_TERM)
                .contractNumber(contractNumber)
                .startDate(startDate)
                .endDate(endDate)
                .workingHours(workingHours != null ? workingHours : DEFAULT_WORKING_HOURS)
                .trialPeriodMonths(trialPeriodMonths != null ? trialPeriodMonths : 0)
                .status(ContractStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 續約
     * @param newEndDate 新的結束日期
     * @throws DomainException 若不是定期合約或已終止
     */
    public void renew(LocalDate newEndDate) {
        if (this.contractType != ContractType.FIXED_TERM) {
            throw new DomainException("NOT_FIXED_TERM", "不定期合約無需續約");
        }
        if (this.status == ContractStatus.TERMINATED) {
            throw new DomainException("CONTRACT_TERMINATED", "已終止的合約不可續約");
        }
        if (newEndDate == null || !newEndDate.isAfter(this.endDate)) {
            throw new DomainException("INVALID_END_DATE", "新的結束日期必須晚於目前結束日期");
        }
        this.endDate = newEndDate;
        this.status = ContractStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 終止合約
     */
    public void terminate() {
        if (this.status == ContractStatus.TERMINATED) {
            throw new DomainException("ALREADY_TERMINATED", "合約已終止");
        }
        this.status = ContractStatus.TERMINATED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 標記為已到期
     */
    public void markAsExpired() {
        if (this.status != ContractStatus.ACTIVE) {
            return;
        }
        if (this.contractType == ContractType.INDEFINITE) {
            return;
        }
        if (this.endDate != null && this.endDate.isBefore(LocalDate.now())) {
            this.status = ContractStatus.EXPIRED;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 更新附件
     * @param attachmentUrl 附件 URL
     */
    public void updateAttachment(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== 查詢方法 ====================

    /**
     * 是否為不定期合約
     * @return 是否為不定期
     */
    public boolean isIndefinite() {
        return this.contractType.isIndefinite();
    }

    /**
     * 是否生效中
     * @return 是否生效
     */
    public boolean isActive() {
        return this.status.isActive();
    }

    /**
     * 是否即將到期 (30天內)
     * @return 是否即將到期
     */
    public boolean isExpiringSoon() {
        if (this.contractType == ContractType.INDEFINITE || this.endDate == null) {
            return false;
        }
        LocalDate thirtyDaysLater = LocalDate.now().plusDays(30);
        return this.endDate.isBefore(thirtyDaysLater) && this.endDate.isAfter(LocalDate.now());
    }

    /**
     * 計算合約剩餘天數
     * @return 剩餘天數，不定期合約返回 -1
     */
    public long getRemainingDays() {
        if (this.contractType == ContractType.INDEFINITE || this.endDate == null) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), this.endDate);
    }

    // ==================== 驗證方法 ====================

    private static void validateEmployeeId(UUID employeeId) {
        if (employeeId == null) {
            throw new DomainException("EMPLOYEE_ID_REQUIRED", "員工 ID 不可為空");
        }
    }

    private static void validateContractNumber(String contractNumber) {
        if (contractNumber == null || contractNumber.isBlank()) {
            throw new DomainException("CONTRACT_NUMBER_REQUIRED", "合約編號不可為空");
        }
        if (contractNumber.length() > 100) {
            throw new DomainException("CONTRACT_NUMBER_TOO_LONG", "合約編號長度不可超過100字元");
        }
    }

    private static void validateStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new DomainException("START_DATE_REQUIRED", "合約開始日期不可為空");
        }
    }

    private static void validateEndDate(LocalDate startDate, LocalDate endDate) {
        if (endDate == null) {
            throw new DomainException("END_DATE_REQUIRED", "定期合約必須指定結束日期");
        }
        if (!endDate.isAfter(startDate)) {
            throw new DomainException("INVALID_END_DATE", "結束日期必須晚於開始日期");
        }
    }
}
