package com.company.hrms.payroll.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * IPayrollRunRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakePayrollRunRepository
        extends InMemoryBaseRepository<PayrollRun, RunId>
        implements IPayrollRunRepository {

    public FakePayrollRunRepository() {
        super(PayrollRun.class, PayrollRun::getId);
    }

    @Override
    public PayrollRun save(PayrollRun payrollRun) {
        return doSave(payrollRun);
    }

    @Override
    public Optional<PayrollRun> findById(RunId id) {
        return super.findById(id);
    }

    @Override
    public List<PayrollRun> findByOrganization(String organizationId) {
        return findByField("organizationId", organizationId);
    }

    @Override
    public Optional<PayrollRun> findByOrganizationAndPeriod(String organizationId, PayPeriod payPeriod) {
        // 比對組織 ID 與計薪期間（年月）
        return findByField("organizationId", organizationId).stream()
                .filter(run -> run.getPayPeriod() != null && run.getPayPeriod().equals(payPeriod))
                .findFirst();
    }

    @Override
    public Page<PayrollRun> findAll(QueryGroup group, Pageable pageable) {
        return super.findPage(group, pageable);
    }
}
