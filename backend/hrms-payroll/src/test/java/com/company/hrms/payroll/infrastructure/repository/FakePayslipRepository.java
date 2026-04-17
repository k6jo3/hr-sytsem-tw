package com.company.hrms.payroll.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.valueobject.PayslipId;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IPayslipRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakePayslipRepository
        extends InMemoryBaseRepository<Payslip, PayslipId>
        implements IPayslipRepository {

    public FakePayslipRepository() {
        super(Payslip.class, Payslip::getId);
    }

    @Override
    public Payslip save(Payslip payslip) {
        return doSave(payslip);
    }

    @Override
    public void saveAllPayslips(List<Payslip> payslips) {
        // 逐筆儲存
        for (Payslip payslip : payslips) {
            doSave(payslip);
        }
    }

    @Override
    public Optional<Payslip> findById(PayslipId id) {
        return super.findById(id);
    }

    @Override
    public List<Payslip> findByPayrollRun(RunId runId) {
        return findByField("payrollRunId", runId);
    }

    @Override
    public List<Payslip> findByEmployeeId(String employeeId) {
        return findByField("employeeId", employeeId);
    }

    @Override
    public List<Payslip> findByEmployeeAndYear(String employeeId, int year) {
        // 篩選員工 ID 且計薪期間在指定年度的薪資單
        return findByField("employeeId", employeeId).stream()
                .filter(p -> p.getPayPeriod() != null
                        && p.getPayPeriod().getStartDate().getYear() == year)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Payslip> findAll(QueryGroup group, Pageable pageable) {
        return super.findPage(group, pageable);
    }
}
