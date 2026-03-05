package com.company.hrms.payroll.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.common.query.QueryGroup;

/**
 * 法扣款 Repository
 */
public interface ILegalDeductionRepository {

    void save(LegalDeduction deduction);

    Optional<LegalDeduction> findById(DeductionId id);

    List<LegalDeduction> findByEmployeeId(String employeeId);

    /**
     * 查詢員工執行中的法扣（ACTIVE），依優先順序排列
     */
    List<LegalDeduction> findActiveByEmployeeId(String employeeId);

    List<LegalDeduction> findByQuery(QueryGroup query);
}
