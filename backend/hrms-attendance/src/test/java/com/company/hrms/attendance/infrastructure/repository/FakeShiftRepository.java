package com.company.hrms.attendance.infrastructure.repository;

import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.model.valueobject.ShiftId;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * IShiftRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeShiftRepository
        extends InMemoryBaseRepository<Shift, ShiftId>
        implements IShiftRepository {

    public FakeShiftRepository() {
        super(Shift.class, Shift::getId);
    }

    @Override
    public Optional<Shift> findById(ShiftId id) {
        return super.findById(id);
    }

    @Override
    public List<Shift> findAll() {
        return super.findAll();
    }

    @Override
    public List<Shift> findByQuery(QueryGroup query) {
        return findAll(query);
    }

    @Override
    public void delete(ShiftId id) {
        super.deleteById(id);
    }
@Override    public void save(Shift shift) {        doSave(shift);    }
}
