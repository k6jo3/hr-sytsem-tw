package com.company.hrms.attendance.infrastructure.repository;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;
import com.company.hrms.attendance.domain.repository.IAttendanceRecordRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IAttendanceRecordRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeAttendanceRecordRepository
        extends InMemoryBaseRepository<AttendanceRecord, RecordId>
        implements IAttendanceRecordRepository {

    public FakeAttendanceRecordRepository() {
        super(AttendanceRecord.class, AttendanceRecord::getId);
    }

    @Override
    public Optional<AttendanceRecord> findById(RecordId id) {
        return super.findById(id);
    }

    @Override
    public List<AttendanceRecord> findByEmployeeIdAndDate(String employeeId, LocalDate date) {
        return findAll().stream()
                .filter(r -> employeeId.equals(r.getEmployeeId()))
                .filter(r -> date.equals(r.getDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> findByEmployeeIdAndDateRange(String employeeId, LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(r -> employeeId.equals(r.getEmployeeId()))
                .filter(r -> !r.getDate().isBefore(startDate) && !r.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceRecord> findByQuery(QueryGroup query) {
        return findAll(query);
    }

    @Override
    public Page<AttendanceRecord> findPageByQuery(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    @Override
    public void delete(RecordId id) {
        super.deleteById(id);
    }

    @Override
    public List<String> findEmployeeIdsWithRecordOnDate(LocalDate date) {
        return findAll().stream()
                .filter(r -> date.equals(r.getDate()))
                .map(AttendanceRecord::getEmployeeId)
                .distinct()
                .collect(Collectors.toList());
    }
@Override    public void save(AttendanceRecord attendanceRecord) {        doSave(attendanceRecord);    }
}
