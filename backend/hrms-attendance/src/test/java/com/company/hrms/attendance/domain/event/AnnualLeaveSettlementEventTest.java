package com.company.hrms.attendance.domain.event;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 特休結算事件單元測試
 */
class AnnualLeaveSettlementEventTest {

    @Test
    @DisplayName("應正確建立離職特休結算事件")
    void shouldCreateSettlementEvent() {
        AnnualLeaveSettlementEvent event = new AnnualLeaveSettlementEvent(
            "EMP-001",
            new BigDecimal("5.5"),
            LocalDate.of(2026, 3, 17),
            new BigDecimal("1500.00")
        );

        assertEquals("EMP-001", event.getEmployeeId());
        assertEquals(new BigDecimal("5.5"), event.getRemainingDays());
        assertEquals(LocalDate.of(2026, 3, 17), event.getTerminationDate());
        assertEquals(new BigDecimal("1500.00"), event.getEstimatedCompensation());
        assertEquals("LeaveBalance", event.getAggregateType());
        assertEquals("EMP-001", event.getAggregateId());
        assertNotNull(event.getEventId());
        assertNotNull(event.getOccurredOn());
    }

    @Test
    @DisplayName("剩餘天數為 0 時也應正常建立")
    void shouldHandleZeroRemainingDays() {
        AnnualLeaveSettlementEvent event = new AnnualLeaveSettlementEvent(
            "EMP-002",
            BigDecimal.ZERO,
            LocalDate.of(2026, 6, 30),
            BigDecimal.ZERO
        );

        assertEquals(BigDecimal.ZERO, event.getRemainingDays());
        assertEquals(BigDecimal.ZERO, event.getEstimatedCompensation());
    }
}
