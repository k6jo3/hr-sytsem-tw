package com.company.hrms.payroll.infrastructure.repository;

import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.payroll.domain.model.aggregate.PayrollAdjustment;
import com.company.hrms.payroll.domain.model.valueobject.AdjustmentId;
import com.company.hrms.payroll.domain.model.valueobject.AdjustmentStatus;
import com.company.hrms.payroll.domain.repository.IPayrollAdjustmentRepository;

import java.util.List;
import java.util.Optional;

/**
 * IPayrollAdjustmentRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakePayrollAdjustmentRepository
        extends InMemoryBaseRepository<PayrollAdjustment, AdjustmentId>
        implements IPayrollAdjustmentRepository {

    public FakePayrollAdjustmentRepository() {
        super(PayrollAdjustment.class, PayrollAdjustment::getId);
    }

    // save(void) 與 update(void) 繼承自 InMemoryBaseRepository

    @Override
    public Optional<PayrollAdjustment> findById(AdjustmentId id) {
        return super.findById(id);
    }

    @Override
    public List<PayrollAdjustment> findByEmployeeId(String employeeId) {
        return findByField("employeeId", employeeId);
    }

    @Override
    public List<PayrollAdjustment> findByOriginalPayslipId(String payslipId) {
        return findByField("originalPayslipId", payslipId);
    }

    @Override
    public List<PayrollAdjustment> findByStatus(AdjustmentStatus status) {
        return findByField("status", status);
    }
@Override    public void save(PayrollAdjustment payrollAdjustment) {        doSave(payrollAdjustment);    }
}
