package com.company.hrms.timesheet.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetPeriod;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;

class TimesheetTest {

    private Timesheet timesheet;
    private UUID employeeId;
    private LocalDate weekStart;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        weekStart = LocalDate.now().minusDays(3); // Start earlier this week
        timesheet = Timesheet.create(employeeId, weekStart);
    }

    @Test
    void create_ShouldInitializeCorrectly() {
        assertNotNull(timesheet.getId());
        assertEquals(employeeId, timesheet.getEmployeeId());
        assertEquals(weekStart, timesheet.getPeriodStartDate());
        assertEquals(weekStart.plusDays(6), timesheet.getPeriodEndDate());
        assertEquals(TimesheetPeriod.WEEKLY, timesheet.getPeriodType());
        assertEquals(TimesheetStatus.DRAFT, timesheet.getStatus());
        assertEquals(BigDecimal.ZERO, timesheet.getTotalHours());
        assertFalse(timesheet.isLocked());
    }

    @Test
    void addEntry_ValidEntry_ShouldAddSuccessfully() {
        UUID projectId = UUID.randomUUID();
        LocalDate workDate = weekStart;
        TimesheetEntry entry = TimesheetEntry.create(projectId, null, workDate, new BigDecimal("8.0"), "Work");

        timesheet.addEntry(entry);

        assertEquals(1, timesheet.getEntries().size());
        assertEquals(new BigDecimal("8.0"), timesheet.getTotalHours());
    }

    @Test
    void addEntry_LockedTimesheet_ShouldThrowException() {
        timesheet.lock();

        UUID projectId = UUID.randomUUID();
        LocalDate workDate = weekStart;
        TimesheetEntry entry = TimesheetEntry.create(projectId, null, workDate, new BigDecimal("8.0"), "Work");

        assertThrows(DomainException.class, () -> timesheet.addEntry(entry));
    }

    @Test
    void addEntry_FutureDate_ShouldThrowException() {
        UUID projectId = UUID.randomUUID();
        LocalDate futureDate = LocalDate.now().plusDays(1);
        TimesheetEntry entry = TimesheetEntry.create(projectId, null, futureDate, new BigDecimal("8.0"), "Future Work");

        assertThrows(DomainException.class, () -> timesheet.addEntry(entry));
    }

    @Test
    void addEntry_ExceedDailyLimit_ShouldThrowException() {
        UUID projectId1 = UUID.randomUUID();
        UUID projectId2 = UUID.randomUUID();
        LocalDate workDate = weekStart;

        timesheet.addEntry(TimesheetEntry.create(projectId1, null, workDate, new BigDecimal("20.0"), "Work 1"));

        TimesheetEntry entry2 = TimesheetEntry.create(projectId2, null, workDate, new BigDecimal("5.0"), "Work 2");

        // 20 + 5 > 24
        assertThrows(DomainException.class, () -> timesheet.addEntry(entry2));
    }

    @Test
    void submit_ValidState_ShouldUpdateStatus() {
        UUID projectId = UUID.randomUUID();
        timesheet.addEntry(TimesheetEntry.create(projectId, null, weekStart, new BigDecimal("8.0"), "Work"));

        timesheet.submit();

        assertEquals(TimesheetStatus.SUBMITTED, timesheet.getStatus());
        assertNotNull(timesheet.getSubmittedAt());
    }

    @Test
    void submit_EmptyEntries_ShouldThrowException() {
        assertThrows(DomainException.class, () -> timesheet.submit());
    }
}
