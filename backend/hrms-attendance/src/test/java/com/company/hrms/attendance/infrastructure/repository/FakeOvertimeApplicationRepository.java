package com.company.hrms.attendance.infrastructure.repository;

import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IOvertimeApplicationRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeOvertimeApplicationRepository
        extends InMemoryBaseRepository<OvertimeApplication, OvertimeId>
        implements IOvertimeApplicationRepository {

    public FakeOvertimeApplicationRepository() {
        super(OvertimeApplication.class, OvertimeApplication::getId);
    }

    @Override
    public Optional<OvertimeApplication> findById(OvertimeId id) {
        return super.findById(id);
    }

    @Override
    public List<OvertimeApplication> findByEmployeeId(String employeeId) {
        return findByField("employeeId", employeeId);
    }

    @Override
    public List<OvertimeApplication> findByEmployeeIdAndMonth(String employeeId, int year, int month) {
        return findAll().stream()
                .filter(o -> employeeId.equals(o.getEmployeeId()))
                .filter(o -> o.getOvertimeDate().getYear() == year)
                .filter(o -> o.getOvertimeDate().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    @Override
    public List<OvertimeApplication> findByQuery(QueryGroup query) {
        return findAll(query);
    }

    @Override
    public void delete(OvertimeId id) {
        super.deleteById(id);
    }
@Override    public void save(OvertimeApplication overtimeApplication) {        doSave(overtimeApplication);    }
}
