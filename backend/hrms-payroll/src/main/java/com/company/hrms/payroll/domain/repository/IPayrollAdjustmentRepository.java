package com.company.hrms.payroll.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.payroll.domain.model.aggregate.PayrollAdjustment;
import com.company.hrms.payroll.domain.model.valueobject.AdjustmentId;
import com.company.hrms.payroll.domain.model.valueobject.AdjustmentStatus;

/**
 * 薪資調整單 Repository 介面
 */
public interface IPayrollAdjustmentRepository {

    void save(PayrollAdjustment adjustment);

    Optional<PayrollAdjustment> findById(AdjustmentId id);

    List<PayrollAdjustment> findByEmployeeId(String employeeId);

    List<PayrollAdjustment> findByOriginalPayslipId(String payslipId);

    List<PayrollAdjustment> findByStatus(AdjustmentStatus status);
}
