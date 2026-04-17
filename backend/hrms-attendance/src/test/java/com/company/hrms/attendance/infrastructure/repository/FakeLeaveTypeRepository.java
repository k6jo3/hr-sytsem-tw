package com.company.hrms.attendance.infrastructure.repository;

import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * ILeaveTypeRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeLeaveTypeRepository
        extends InMemoryBaseRepository<LeaveType, LeaveTypeId>
        implements ILeaveTypeRepository {

    public FakeLeaveTypeRepository() {
        super(LeaveType.class, LeaveType::getId);
    }

    @Override
    public Optional<LeaveType> findById(LeaveTypeId id) {
        return super.findById(id);
    }

    @Override
    public Optional<LeaveType> findByCode(String code) {
        return findOneByField("code", code);
    }

    @Override
    public List<LeaveType> findAll() {
        return super.findAll();
    }

    @Override
    public List<LeaveType> findByQuery(QueryGroup query) {
        return findAll(query);
    }

    @Override
    public void delete(LeaveTypeId id) {
        super.deleteById(id);
    }
@Override    public void save(LeaveType leaveType) {        doSave(leaveType);    }
}
