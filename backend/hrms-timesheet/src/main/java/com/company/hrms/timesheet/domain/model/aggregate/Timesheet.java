package com.company.hrms.timesheet.domain.model.aggregate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.timesheet.domain.event.TimesheetApprovedEvent;
import com.company.hrms.timesheet.domain.event.TimesheetRejectedEvent;
import com.company.hrms.timesheet.domain.event.TimesheetSubmittedEvent;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetPeriod;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;

import lombok.Getter;

@Getter
public class Timesheet extends AggregateRoot<TimesheetId> {

    private UUID employeeId;
    private TimesheetPeriod periodType;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;
    private List<TimesheetEntry> entries = new ArrayList<>();
    private BigDecimal totalHours = BigDecimal.ZERO;
    private TimesheetStatus status;
    private LocalDateTime submittedAt;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private boolean isLocked;

    // Private constructor for factory method
    private Timesheet(TimesheetId id) {
        super(id);
    }

    public static Timesheet create(UUID employeeId, LocalDate weekStartDate) {
        Timesheet ts = new Timesheet(TimesheetId.generate());
        ts.employeeId = employeeId;
        ts.periodType = TimesheetPeriod.WEEKLY;
        ts.periodStartDate = weekStartDate;
        ts.periodEndDate = weekStartDate.plusDays(6);
        ts.status = TimesheetStatus.DRAFT;
        ts.isLocked = false;
        return ts;
    }

    // Reconstruction factory
    public static Timesheet reconstitute(
            TimesheetId id,
            UUID employeeId,
            TimesheetPeriod periodType,
            LocalDate periodStartDate,
            LocalDate periodEndDate,
            List<TimesheetEntry> entries,
            BigDecimal totalHours,
            TimesheetStatus status,
            LocalDateTime submittedAt,
            UUID approvedBy,
            LocalDateTime approvedAt,
            String rejectionReason,
            boolean isLocked) {

        Timesheet ts = new Timesheet(id);
        ts.employeeId = employeeId;
        ts.periodType = periodType;
        ts.periodStartDate = periodStartDate;
        ts.periodEndDate = periodEndDate;
        ts.entries = entries != null ? entries : new ArrayList<>();
        ts.totalHours = totalHours;
        ts.status = status;
        ts.submittedAt = submittedAt;
        ts.approvedBy = approvedBy;
        ts.approvedAt = approvedAt;
        ts.rejectionReason = rejectionReason;
        ts.isLocked = isLocked;
        return ts;
    }

    public void addEntry(TimesheetEntry entry) {
        if (this.isLocked) {
            throw new DomainException("工時表已鎖定，無法修改");
        }

        if (entry.getWorkDate().isAfter(LocalDate.now())) {
            throw new DomainException("不可回報未來日期的工時");
        }

        // Check daily limit (24 hours)
        BigDecimal dailyTotal = this.entries.stream()
                .filter(e -> e.getWorkDate().equals(entry.getWorkDate()))
                .map(TimesheetEntry::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (dailyTotal.add(entry.getHours()).compareTo(new BigDecimal("24")) > 0) {
            throw new DomainException("單日工時不可超過24小時");
        }

        this.entries.add(entry);
        recalculateTotal();
    }

    public void updateEntry(UUID entryId, TimesheetEntry updatedEntry) {
        if (this.isLocked) {
            throw new DomainException("工時表已鎖定，無法修改");
        }

        TimesheetEntry existing = this.entries.stream()
                .filter(e -> e.getId().equals(entryId))
                .findFirst()
                .orElseThrow(() -> new DomainException("找不到此工時明細"));

        // Check daily limit
        BigDecimal dailyTotalWithoutCurrent = this.entries.stream()
                .filter(e -> e.getWorkDate().equals(updatedEntry.getWorkDate()) && !e.getId().equals(entryId))
                .map(TimesheetEntry::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (dailyTotalWithoutCurrent.add(updatedEntry.getHours()).compareTo(new BigDecimal("24")) > 0) {
            throw new DomainException("單日工時不可超過24小時");
        }

        // Update fields (Keep ID)
        existing.setProjectId(updatedEntry.getProjectId());
        existing.setTaskId(updatedEntry.getTaskId());
        existing.setWorkDate(updatedEntry.getWorkDate());
        existing.setHours(updatedEntry.getHours());
        existing.setDescription(updatedEntry.getDescription());

        recalculateTotal();
    }

    public void removeEntry(UUID entryId) {
        if (this.isLocked) {
            throw new DomainException("工時表已鎖定，無法修改");
        }

        boolean removed = this.entries.removeIf(e -> e.getId().equals(entryId));
        if (!removed) {
            throw new DomainException("找不到此工時明細");
        }
        recalculateTotal();
    }

    public void submit() {
        if (this.entries.isEmpty()) {
            throw new DomainException("至少需要一筆工時記錄");
        }
        this.status = TimesheetStatus.PENDING;
        this.submittedAt = LocalDateTime.now();

        registerEvent(new TimesheetSubmittedEvent(
                this.getId().getValue(),
                this.employeeId,
                this.totalHours,
                this.periodStartDate,
                this.periodEndDate));
    }

    public void approve(UUID approverId) {
        if (this.status != TimesheetStatus.PENDING) {
            throw new DomainException("只能簽核狀態為提交的工時表");
        }
        this.status = TimesheetStatus.APPROVED;
        this.approvedBy = approverId;
        this.approvedAt = LocalDateTime.now();
        this.isLocked = true;

        registerEvent(new TimesheetApprovedEvent(
                this.getId().getValue(),
                this.employeeId,
                approverId,
                this.approvedAt));
    }

    public void reject(UUID approverId, String reason) {
        if (this.status != TimesheetStatus.PENDING) {
            throw new DomainException("只能退回狀態為提交的工時表");
        }
        this.status = TimesheetStatus.REJECTED;
        this.approvedBy = approverId;
        this.rejectionReason = reason;
        this.isLocked = false;

        registerEvent(new TimesheetRejectedEvent(
                this.getId().getValue(),
                this.employeeId,
                reason));
    }

    public void lock() {
        this.isLocked = true;
    }

    private void recalculateTotal() {
        this.totalHours = this.entries.stream()
                .map(TimesheetEntry::getHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
