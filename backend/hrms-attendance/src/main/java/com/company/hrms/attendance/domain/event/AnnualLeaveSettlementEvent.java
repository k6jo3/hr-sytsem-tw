package com.company.hrms.attendance.domain.event;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.company.hrms.common.domain.event.DomainEvent;

import lombok.Getter;

/**
 * 特休結算事件
 *
 * <p>當員工離職時，結算其剩餘特休天數及預估折算金額。
 * 供 Payroll 模組訂閱，用於計算離職時的特休未休工資。
 */
@Getter
public class AnnualLeaveSettlementEvent extends DomainEvent {

    /** 員工 ID */
    private final String employeeId;

    /** 剩餘特休天數 */
    private final BigDecimal remainingDays;

    /** 離職日期 */
    private final LocalDate terminationDate;

    /** 預估折算金額（可為 null，由 Payroll 模組計算正式金額） */
    private final BigDecimal estimatedCompensation;

    public AnnualLeaveSettlementEvent(String employeeId, BigDecimal remainingDays,
                                       LocalDate terminationDate, BigDecimal estimatedCompensation) {
        super();
        this.employeeId = employeeId;
        this.remainingDays = remainingDays;
        this.terminationDate = terminationDate;
        this.estimatedCompensation = estimatedCompensation;
    }

    @Override
    public String getAggregateId() {
        return employeeId;
    }

    @Override
    public String getAggregateType() {
        return "LeaveBalance";
    }
}
