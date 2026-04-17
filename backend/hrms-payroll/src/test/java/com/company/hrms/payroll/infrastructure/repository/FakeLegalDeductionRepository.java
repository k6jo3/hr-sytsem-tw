package com.company.hrms.payroll.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentStatus;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ILegalDeductionRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeLegalDeductionRepository
        extends InMemoryBaseRepository<LegalDeduction, DeductionId>
        implements ILegalDeductionRepository {

    public FakeLegalDeductionRepository() {
        super(LegalDeduction.class, LegalDeduction::getId);
    }

    // save(void) 與 update(void) 繼承自 InMemoryBaseRepository

    @Override
    public Optional<LegalDeduction> findById(DeductionId id) {
        return super.findById(id);
    }

    @Override
    public List<LegalDeduction> findByEmployeeId(String employeeId) {
        return findByField("employeeId", employeeId);
    }

    @Override
    public List<LegalDeduction> findActiveByEmployeeId(String employeeId) {
        // 查詢員工執行中的法扣（ACTIVE），依優先順序排列
        return findByField("employeeId", employeeId).stream()
                .filter(d -> d.getStatus() == GarnishmentStatus.ACTIVE)
                .sorted(Comparator.comparingInt(LegalDeduction::getPriority))
                .collect(Collectors.toList());
    }

    @Override
    public List<LegalDeduction> findByQuery(QueryGroup query) {
        return super.findAll(query);
    }
@Override    public void save(LegalDeduction legalDeduction) {        doSave(legalDeduction);    }
}
