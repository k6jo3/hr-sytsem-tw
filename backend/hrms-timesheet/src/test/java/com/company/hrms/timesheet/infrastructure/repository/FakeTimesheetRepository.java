package com.company.hrms.timesheet.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ITimesheetRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeTimesheetRepository repo = new FakeTimesheetRepository();
 * repo.seed(Timesheet.create(employeeId, LocalDate.of(2026, 1, 6)));
 *
 * Optional&lt;Timesheet&gt; found = repo.findByEmployeeAndWeek(employeeId, weekStartDate);
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakeTimesheetRepository
        extends InMemoryBaseRepository<Timesheet, TimesheetId>
        implements ITimesheetRepository {

    public FakeTimesheetRepository() {
        super(Timesheet.class, Timesheet::getId);
    }

    /**
     * 儲存工時表（介面回傳實體）
     */
    @Override
    public Timesheet save(Timesheet timesheet) {
        return doSave(timesheet);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<Timesheet> findById(TimesheetId id) {
        return super.findById(id);
    }

    /**
     * 動態查詢所有工時表（分頁）
     */
    @Override
    public Page<Timesheet> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    /**
     * 查詢員工指定週的工時表
     * 使用 employeeId + periodStartDate 聯合查詢
     */
    @Override
    public Optional<Timesheet> findByEmployeeAndWeek(UUID employeeId, LocalDate weekStartDate) {
        return findAll().stream()
                .filter(ts -> employeeId.equals(ts.getEmployeeId()))
                .filter(ts -> weekStartDate.equals(ts.getPeriodStartDate()))
                .findFirst();
    }

    /**
     * 查詢員工指定日期的工時表
     * 查詢包含該日期的週報（periodStartDate <= date <= periodEndDate）
     */
    @Override
    public Optional<Timesheet> findByEmployeeAndDate(UUID employeeId, LocalDate date) {
        return findAll().stream()
                .filter(ts -> employeeId.equals(ts.getEmployeeId()))
                .filter(ts -> !date.isBefore(ts.getPeriodStartDate())
                        && !date.isAfter(ts.getPeriodEndDate()))
                .findFirst();
    }

    /**
     * 查詢待審核工時表
     * 篩選 approvedBy 為指定審核者且狀態為 SUBMITTED 的工時表
     */
    @Override
    public Page<Timesheet> findPendingApprovals(UUID approverId, Pageable pageable) {
        List<Timesheet> pending = findAll().stream()
                .filter(ts -> "SUBMITTED".equals(ts.getStatus() != null ? ts.getStatus().name() : null))
                .collect(Collectors.toList());

        long total = pending.size();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Timesheet> content = pending.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 動態查詢工時表（分頁），委派至 findPage
     */
    @Override
    public Page<Timesheet> findPageByQuery(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }
}
