package com.company.hrms.payroll.infrastructure.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Condition;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.ItemType;
import com.company.hrms.payroll.domain.model.valueobject.PayrollCycle;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.StructureId;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;
import com.company.hrms.payroll.infrastructure.po.SalaryItemPO;
import com.company.hrms.payroll.infrastructure.po.SalaryStructurePO;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class SalaryStructureRepositoryImpl extends BaseRepository<SalaryStructurePO, String>
        implements ISalaryStructureRepository {

    public SalaryStructureRepositoryImpl(JPAQueryFactory factory) {
        super(factory, SalaryStructurePO.class);
    }

    @Override
    public SalaryStructure save(SalaryStructure structure) {
        SalaryStructurePO po = toPO(structure);
        if (po.getItems() != null) {
            po.getItems().forEach(item -> item.setSalaryStructure(po));
        }

        super.save(po);
        return toDomain(po);
    }

    @Override
    public Optional<SalaryStructure> findById(StructureId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public Optional<SalaryStructure> findByEmployeeId(String employeeId) {
        QueryGroup group = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .build();

        Page<SalaryStructurePO> page = super.findPage(group,
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "effectiveDate")));

        if (page.hasContent()) {
            return Optional.of(toDomain(page.getContent().get(0)));
        }
        return Optional.empty();
    }

    @Override
    public <C> Page<SalaryStructure> findPageByCondition(Condition<C> condition) {
        return super.findPage(condition).map(this::toDomain);
    }

    @Override
    @Deprecated
    public Page<SalaryStructure> findAll(QueryGroup group, Pageable pageable) {
        return super.findPage(group, pageable).map(this::toDomain);
    }

    @Override
    public Optional<SalaryStructure> findByEmployeeAndEffectiveDate(String employeeId, LocalDate effectiveDate) {
        QueryGroup group = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("effectiveDate", Operator.LTE, effectiveDate)
                .orGroup(g -> g
                        .and("endDate", Operator.GTE, effectiveDate)
                        .and("endDate", Operator.IS_NULL, null))
                .build();

        Page<SalaryStructurePO> page = super.findPage(group,
                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "effectiveDate")));

        if (page.hasContent()) {
            return Optional.of(toDomain(page.getContent().get(0)));
        }
        return Optional.empty();
    }

    @Override
    public List<SalaryStructure> findAllActiveByPayrollSystem(String payrollSystem) {
        QueryGroup group = QueryBuilder.where()
                .and("active", Operator.EQ, true)
                .and("payrollSystem", Operator.EQ, payrollSystem)
                .build();

        return super.findAll(group).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private SalaryStructurePO toPO(SalaryStructure domain) {
        SalaryStructurePO po = SalaryStructurePO.builder()
                .structureId(domain.getId() != null ? domain.getId().getValue() : null)
                .employeeId(domain.getEmployeeId())
                .payrollSystem(domain.getPayrollSystem().name())
                .payrollCycle(domain.getPayrollCycle().name())
                .effectiveDate(domain.getEffectiveDate())
                .endDate(domain.getEndDate())
                .monthlySalary(domain.getMonthlySalary())
                .hourlyRate(domain.getHourlyRate())
                .active(domain.isActive())
                .items(new ArrayList<>())
                .build();

        List<SalaryItemPO> itemPOs = domain.getItems().stream()
                .map(this::toItemPO)
                .collect(Collectors.toList());
        po.setItems(itemPOs);

        return po;
    }

    private SalaryItemPO toItemPO(SalaryItem domain) {
        return SalaryItemPO.builder()
                .itemId(domain.getItemId())
                .code(domain.getItemCode())
                .name(domain.getItemName())
                .type(domain.getItemType().name())
                .amount(domain.getAmount())
                // .rate(domain.getRate()) // Removed: Rate does not exist in SalaryItem or
                // SalaryItemPO
                .fixedAmount(domain.isFixedAmount())
                .taxable(domain.isTaxable())
                .insurable(domain.isInsurable())
                .build();
    }

    private SalaryStructure toDomain(SalaryStructurePO po) {
        List<SalaryItem> items = new ArrayList<>();
        if (po.getItems() != null) {
            items = po.getItems().stream()
                    .map(this::toItemDomain)
                    .collect(Collectors.toList());
        }

        // Correct Argument Order for SalaryStructure.reconstruct:
        // (id, employeeId, monthlySalary, hourlyRate, payrollSystem, payrollCycle,
        // items, effectiveDate, endDate, active)
        return SalaryStructure.reconstruct(
                new StructureId(po.getStructureId()),
                po.getEmployeeId(),
                po.getMonthlySalary(),
                po.getHourlyRate(),
                PayrollSystem.valueOf(po.getPayrollSystem()),
                PayrollCycle.valueOf(po.getPayrollCycle()),
                items,
                po.getEffectiveDate(),
                po.getEndDate(),
                po.isActive());
    }

    private SalaryItem toItemDomain(SalaryItemPO po) {
        // Correct Argument Order for SalaryItem.reconstruct:
        // (itemId, itemCode, itemName, type, amount, fixedAmount, taxable, insurable)
        return SalaryItem.reconstruct(
                po.getItemId(),
                po.getCode(),
                po.getName(),
                ItemType.valueOf(po.getType()),
                po.getAmount(),
                // Rate removed
                po.isFixedAmount(),
                po.isTaxable(),
                po.isInsurable());
    }
}
