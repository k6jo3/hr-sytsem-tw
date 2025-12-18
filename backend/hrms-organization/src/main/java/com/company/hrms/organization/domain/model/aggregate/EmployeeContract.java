package com.company.hrms.organization.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.ContractId;
import com.company.hrms.organization.domain.model.valueobject.ContractStatus;
import com.company.hrms.organization.domain.model.valueobject.ContractType;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 員工合約聚合根
 * 管理員工的勞動合約
 */
@Getter
@Builder
public class EmployeeContract {

    /**
     * 合約 ID
     */
    private final ContractId id;

    /**
     * 員工 ID
     */
    private EmployeeId employeeId;

    /**
     * 合約類型 (不定期/定期)
     */
    private ContractType contractType;

    /**
     * 合約開始日期
     */
    private LocalDate startDate;

    /**
     * 合約結束日期 (不定期合約為 null)
     */
    private LocalDate endDate;

    /**
     * 合約狀態
     */
    private ContractStatus status;

    /**
     * 試用期月數
     */
    private Integer probationMonths;

    /**
     * 續約次數
     */
    private Integer renewalCount;

    /**
     * 備註
     */
    private String notes;

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
     */
    public static EmployeeContract createIndefinite(
            EmployeeId employeeId,
            LocalDate startDate,
            Integer probationMonths) {

        validateEmployeeId(employeeId);
        validateStartDate(startDate);

        return EmployeeContract.builder()
                .id(ContractId.generate())
                .employeeId(employeeId)
                .contractType(ContractType.INDEFINITE)
                .startDate(startDate)
                .endDate(null)
                .status(ContractStatus.ACTIVE)
                .probationMonths(probationMonths != null ? probationMonths : 0)
                .renewalCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 建立定期合約
     */
    public static EmployeeContract createFixedTerm(
            EmployeeId employeeId,
            LocalDate startDate,
            LocalDate endDate,
            Integer probationMonths) {

        validateEmployeeId(employeeId);
        validateStartDate(startDate);
        validateEndDate(startDate, endDate);

        return EmployeeContract.builder()
                .id(ContractId.generate())
                .employeeId(employeeId)
                .contractType(ContractType.FIXED_TERM)
                .startDate(startDate)
                .endDate(endDate)
                .status(ContractStatus.ACTIVE)
                .probationMonths(probationMonths != null ? probationMonths : 0)
                .renewalCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 從持久層還原
     */
    public static EmployeeContract reconstitute(
            ContractId id,
            EmployeeId employeeId,
            ContractType contractType,
            LocalDate startDate,
            LocalDate endDate,
            ContractStatus status,
            Integer probationMonths,
            Integer renewalCount,
            String notes) {

        return EmployeeContract.builder()
                .id(id)
                .employeeId(employeeId)
                .contractType(contractType)
                .startDate(startDate)
                .endDate(endDate)
                .status(status)
                .probationMonths(probationMonths)
                .renewalCount(renewalCount)
                .notes(notes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 續約
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
        this.renewalCount = (this.renewalCount != null ? this.renewalCount : 0) + 1;
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
     * 更新備註
     */
    public void updateNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== 查詢方法 ====================

    /**
     * 是否為不定期合約
     */
    public boolean isIndefinite() {
        return this.contractType.isIndefinite();
    }

    /**
     * 是否生效中
     */
    public boolean isActive() {
        return this.status.isActive();
    }

    /**
     * 是否即將到期 (30天內)
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
     */
    public long getRemainingDays() {
        if (this.contractType == ContractType.INDEFINITE || this.endDate == null) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), this.endDate);
    }

    // ==================== 驗證方法 ====================

    private static void validateEmployeeId(EmployeeId employeeId) {
        if (employeeId == null) {
            throw new DomainException("EMPLOYEE_ID_REQUIRED", "員工 ID 不可為空");
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
