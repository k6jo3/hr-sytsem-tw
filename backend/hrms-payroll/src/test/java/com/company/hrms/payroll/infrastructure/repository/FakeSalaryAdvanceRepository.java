package com.company.hrms.payroll.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceStatus;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ISalaryAdvanceRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeSalaryAdvanceRepository
        extends InMemoryBaseRepository<SalaryAdvance, AdvanceId>
        implements ISalaryAdvanceRepository {

    public FakeSalaryAdvanceRepository() {
        super(SalaryAdvance.class, SalaryAdvance::getId);
    }

    // save(void) 與 update(void) 繼承自 InMemoryBaseRepository

    @Override
    public Optional<SalaryAdvance> findById(AdvanceId id) {
        return super.findById(id);
    }

    @Override
    public List<SalaryAdvance> findByEmployeeId(String employeeId) {
        return findByField("employeeId", employeeId);
    }

    @Override
    public List<SalaryAdvance> findActiveByEmployeeId(String employeeId) {
        // 查詢員工進行中的預借（DISBURSED 或 REPAYING）
        return findByField("employeeId", employeeId).stream()
                .filter(a -> a.getStatus() == AdvanceStatus.DISBURSED
                        || a.getStatus() == AdvanceStatus.REPAYING)
                .collect(Collectors.toList());
    }

    @Override
    public List<SalaryAdvance> findByQuery(QueryGroup query) {
        return super.findAll(query);
    }
@Override    public void save(SalaryAdvance salaryAdvance) {        doSave(salaryAdvance);    }
}
