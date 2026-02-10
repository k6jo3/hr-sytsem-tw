package com.company.hrms.timesheet.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.domain.event.DomainEvent;
import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBatchBaseRepository;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;
import com.company.hrms.timesheet.infrastructure.entity.TimesheetEntity;
import com.company.hrms.timesheet.infrastructure.entity.TimesheetEntryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class TimesheetRepositoryImpl extends CommandBatchBaseRepository<TimesheetEntity, UUID>
        implements ITimesheetRepository {

    private final EventPublisher eventPublisher;

    public TimesheetRepositoryImpl(JPAQueryFactory factory, EventPublisher eventPublisher) {
        super(factory, TimesheetEntity.class);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Timesheet save(Timesheet timesheet) {
        TimesheetEntity entity = toEntity(timesheet);
        super.save(entity);

        // Publish events
        List<DomainEvent> events = timesheet.getDomainEvents();
        if (!events.isEmpty()) {
            events.forEach(eventPublisher::publish);
            timesheet.clearDomainEvents();
        }

        return timesheet;
    }

    @Override
    public Optional<Timesheet> findById(TimesheetId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Page<Timesheet> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    @Override
    public Page<Timesheet> findPageByQuery(QueryGroup query, Pageable pageable) {
        return super.findPageDistinct(query, pageable).map(this::toDomain);
    }

    @Override
    public Optional<Timesheet> findByEmployeeAndWeek(UUID employeeId, LocalDate weekStartDate) {
        QueryGroup query = QueryBuilder.where()
                .eq("employeeId", employeeId)
                .eq("periodStartDate", weekStartDate)
                .build();

        return super.findAll(query).stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public Optional<Timesheet> findByEmployeeAndDate(UUID employeeId, LocalDate date) {
        // 尋找涵蓋此日期的工時表
        // 條件: employeeId = ? AND periodStartDate <= date AND periodEndDate >= date
        QueryGroup query = QueryBuilder.where()
                .eq("employeeId", employeeId)
                .lte("periodStartDate", date)
                .gte("periodEndDate", date)
                .build();

        return super.findAll(query).stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public Page<Timesheet> findPendingApprovals(UUID approverId, Pageable pageable) {
        // 假設邏輯：尋找所有已提交的工時表
        // 實際邏輯可能需要過濾該審核者管理的專案或部門
        // 目前僅查詢狀態為 PENDING 的工時表
        // 特定的 PM 邏輯可能屬於 Service 層或獨立的查詢建構

        QueryGroup query = QueryBuilder.where()
                .eq("status", "PENDING")
                .build();

        return super.findPage(query, pageable).map(this::toDomain);
    }

    // ================= 實體映射 =================

    private TimesheetEntity toEntity(Timesheet domain) {
        TimesheetEntity entity = new TimesheetEntity();
        entity.setTimesheetId(domain.getId().getValue());
        entity.setEmployeeId(domain.getEmployeeId());
        entity.setPeriodType(domain.getPeriodType());
        entity.setPeriodStartDate(domain.getPeriodStartDate());
        entity.setPeriodEndDate(domain.getPeriodEndDate());
        entity.setTotalHours(domain.getTotalHours());
        entity.setStatus(domain.getStatus());
        entity.setSubmittedAt(domain.getSubmittedAt());
        entity.setApprovedBy(domain.getApprovedBy());
        entity.setApprovedAt(domain.getApprovedAt());
        entity.setRejectionReason(domain.getRejectionReason());
        entity.setLocked(domain.isLocked());

        List<TimesheetEntryEntity> entryEntities = domain.getEntries().stream()
                .map(e -> {
                    TimesheetEntryEntity ee = new TimesheetEntryEntity();
                    ee.setEntryId(e.getId());
                    ee.setProjectId(e.getProjectId());
                    ee.setTaskId(e.getTaskId());
                    ee.setWorkDate(e.getWorkDate());
                    ee.setHours(e.getHours());
                    ee.setDescription(e.getDescription());
                    ee.setCreatedAt(e.getCreatedAt());
                    return ee;
                })
                .collect(Collectors.toList());

        entity.setEntries(entryEntities);
        return entity;
    }

    private Timesheet toDomain(TimesheetEntity entity) {
        List<TimesheetEntry> entries = entity.getEntries().stream()
                .map(e -> {
                    TimesheetEntry te = new TimesheetEntry();
                    te.setId(e.getEntryId());
                    te.setProjectId(e.getProjectId());
                    te.setTaskId(e.getTaskId());
                    te.setWorkDate(e.getWorkDate());
                    te.setHours(e.getHours());
                    te.setDescription(e.getDescription());
                    te.setCreatedAt(e.getCreatedAt());
                    return te;
                })
                .collect(Collectors.toList());

        return Timesheet.reconstitute(
                new TimesheetId(entity.getTimesheetId()),
                entity.getEmployeeId(),
                entity.getPeriodType(),
                entity.getPeriodStartDate(),
                entity.getPeriodEndDate(),
                entries,
                entity.getTotalHours(),
                entity.getStatus(),
                entity.getSubmittedAt(),
                entity.getApprovedBy(),
                entity.getApprovedAt(),
                entity.getRejectionReason(),
                entity.isLocked());
    }
}
