package com.company.hrms.payroll.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.common.query.QueryGroup;

/**
 * 薪資預借 Repository
 */
public interface ISalaryAdvanceRepository {

    void save(SalaryAdvance advance);

    Optional<SalaryAdvance> findById(AdvanceId id);

    List<SalaryAdvance> findByEmployeeId(String employeeId);

    /**
     * 查詢員工進行中的預借（DISBURSED 或 REPAYING）
     */
    List<SalaryAdvance> findActiveByEmployeeId(String employeeId);

    List<SalaryAdvance> findByQuery(QueryGroup query);
}
