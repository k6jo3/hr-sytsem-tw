package com.company.hrms.attendance.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.attendance.domain.model.valueobject.BalanceId;
import com.company.hrms.attendance.domain.model.valueobject.ExpiryPolicy;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.model.valueobject.OverdrawPolicy;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

@Getter
public class LeaveBalance extends AggregateRoot<BalanceId> {

    private String employeeId;
    private LeaveTypeId leaveTypeId;
    private int year;
    private BigDecimal totalDays;
    private BigDecimal usedDays;
    private BigDecimal carryOverDays;
    private LocalDate expiryDate;

    public LeaveBalance(BalanceId id, String employeeId, LeaveTypeId leaveTypeId,
            int year, BigDecimal totalDays) {
        super(id);
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = BigDecimal.ZERO;
        this.carryOverDays = BigDecimal.ZERO;
    }

    /**
     * 扣除假期天數（含超額請假政策判斷）
     *
     * @param days           扣除天數
     * @param overdrawPolicy 超額政策
     * @return 實際可扣除天數（不足時依政策可能小於請求天數）
     */
    public BigDecimal deductWithPolicy(BigDecimal days, OverdrawPolicy overdrawPolicy) {
        if (days.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣除天數必須為正數");
        }

        BigDecimal available = getAvailableDays();
        if (available.compareTo(days) >= 0) {
            this.usedDays = this.usedDays.add(days);
            return days;
        }

        // 餘額不足
        switch (overdrawPolicy) {
            case DENY:
                throw new IllegalStateException("假期餘額不足，剩餘: " + available + " 天");
            case CONVERT_TO_PERSONAL:
                // 僅扣除可用部分，超出部分由呼叫端轉為事假
                this.usedDays = this.usedDays.add(available);
                return available;
            case ADVANCE:
                // 允許預支，全額扣除（可能超出 totalDays）
                this.usedDays = this.usedDays.add(days);
                return days;
            default:
                throw new IllegalStateException("假期餘額不足");
        }
    }

    /**
     * 扣除假期天數（預設 DENY 政策，向後相容）
     */
    public void deduct(BigDecimal days) {
        deductWithPolicy(days, OverdrawPolicy.DENY);
    }

    public void restore(BigDecimal days) {
        if (days.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("復原天數必須為正數");
        }
        if (this.usedDays.compareTo(days) < 0) {
            throw new IllegalArgumentException("復原天數不可超過已使用天數");
        }
        this.usedDays = this.usedDays.subtract(days);
    }

    /**
     * 可用天數 = 年度額度 + 結轉天數 - 已使用天數
     */
    public BigDecimal getAvailableDays() {
        return totalDays.add(carryOverDays).subtract(usedDays);
    }

    /**
     * 剩餘天數（不含結轉）
     */
    public BigDecimal getRemainingDays() {
        return totalDays.subtract(usedDays);
    }

    /**
     * 年度結算：依政策處理未休天數
     *
     * @param expiryPolicy 未休處理政策
     * @return 未休天數（供後續處理：結轉/折薪/作廢）
     */
    public BigDecimal settleYear(ExpiryPolicy expiryPolicy) {
        BigDecimal unused = getAvailableDays();
        if (unused.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        // 不管政策如何，都回傳未休天數供呼叫端處理
        // CARRYOVER: 呼叫端將未休天數加到下年度的 carryOverDays
        // PAY_COMPENSATION: 呼叫端發事件通知薪資模組折算工資
        // FORFEIT: 呼叫端直接歸零
        return unused;
    }

    /**
     * 設定結轉天數（由上年度結算帶入）
     */
    public void addCarryOver(BigDecimal days) {
        if (days.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("結轉天數不可為負數");
        }
        this.carryOverDays = this.carryOverDays.add(days);
    }

    /**
     * 設定到期日
     */
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    private LeaveBalance(BalanceId id, String employeeId, LeaveTypeId leaveTypeId,
            int year, BigDecimal totalDays, BigDecimal usedDays,
            BigDecimal carryOverDays, LocalDate expiryDate) {
        super(id);
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.year = year;
        this.totalDays = totalDays;
        this.usedDays = usedDays;
        this.carryOverDays = carryOverDays != null ? carryOverDays : BigDecimal.ZERO;
        this.expiryDate = expiryDate;
    }

    public static LeaveBalance reconstitute(BalanceId id, String employeeId, LeaveTypeId leaveTypeId,
            int year, BigDecimal totalDays, BigDecimal usedDays) {
        return new LeaveBalance(id, employeeId, leaveTypeId, year, totalDays, usedDays,
                BigDecimal.ZERO, null);
    }

    public static LeaveBalance reconstitute(BalanceId id, String employeeId, LeaveTypeId leaveTypeId,
            int year, BigDecimal totalDays, BigDecimal usedDays,
            BigDecimal carryOverDays, LocalDate expiryDate) {
        return new LeaveBalance(id, employeeId, leaveTypeId, year, totalDays, usedDays,
                carryOverDays, expiryDate);
    }
}
