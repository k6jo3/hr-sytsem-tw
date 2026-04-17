package com.company.hrms.payroll.infrastructure.repository;

import com.company.hrms.common.query.Condition;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.valueobject.StructureId;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ISalaryStructureRepository 的 InMemory 實作
 * 用於單元測試，不需 Spring Context 或資料庫
 */
public class FakeSalaryStructureRepository
        extends InMemoryBaseRepository<SalaryStructure, StructureId>
        implements ISalaryStructureRepository {

    public FakeSalaryStructureRepository() {
        super(SalaryStructure.class, SalaryStructure::getId);
    }

    @Override
    public SalaryStructure save(SalaryStructure structure) {
        return doSave(structure);
    }

    @Override
    public Optional<SalaryStructure> findById(StructureId id) {
        return super.findById(id);
    }

    @Override
    public Optional<SalaryStructure> findByEmployeeId(String employeeId) {
        // 取最新生效的薪資結構（依生效日降序，取第一筆 active 的）
        return findByField("employeeId", employeeId).stream()
                .filter(SalaryStructure::isActive)
                .max(Comparator.comparing(SalaryStructure::getEffectiveDate));
    }

    @Override
    public <C> Page<SalaryStructure> findPageByCondition(Condition<C> condition) {
        return super.findPage(condition.toQueryGroup(), condition.toPageable());
    }

    @Override
    @Deprecated
    public Page<SalaryStructure> findAll(QueryGroup group, Pageable pageable) {
        return super.findPage(group, pageable);
    }

    @Override
    public Optional<SalaryStructure> findByEmployeeAndEffectiveDate(String employeeId, LocalDate effectiveDate) {
        // 查詢該日期生效的結構：effectiveDate <= 指定日期，且 endDate 為 null 或 > 指定日期
        return findByField("employeeId", employeeId).stream()
                .filter(s -> s.isActive())
                .filter(s -> s.getEffectiveDate() != null && !s.getEffectiveDate().isAfter(effectiveDate))
                .filter(s -> s.getEndDate() == null || s.getEndDate().isAfter(effectiveDate))
                .max(Comparator.comparing(SalaryStructure::getEffectiveDate));
    }

    @Override
    public List<SalaryStructure> findAllActiveByPayrollSystem(String payrollSystem) {
        return findAll().stream()
                .filter(SalaryStructure::isActive)
                .filter(s -> s.getPayrollSystem() != null
                        && s.getPayrollSystem().name().equals(payrollSystem))
                .collect(Collectors.toList());
    }
}
