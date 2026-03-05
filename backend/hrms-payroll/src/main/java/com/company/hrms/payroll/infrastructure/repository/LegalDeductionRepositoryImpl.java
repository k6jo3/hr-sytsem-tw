package com.company.hrms.payroll.infrastructure.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.model.valueobject.DeductionId;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentStatus;
import com.company.hrms.payroll.domain.model.valueobject.GarnishmentType;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;
import com.company.hrms.payroll.infrastructure.po.LegalDeductionPO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 法扣款 Repository 實作
 */
@Repository
public class LegalDeductionRepositoryImpl extends BaseRepository<LegalDeductionPO, String>
        implements ILegalDeductionRepository {

    public LegalDeductionRepositoryImpl(JPAQueryFactory queryFactory) {
        super(queryFactory, LegalDeductionPO.class);
    }

    @Override
    public Optional<LegalDeduction> findById(DeductionId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<LegalDeduction> findByEmployeeId(String employeeId) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<LegalDeduction> findActiveByEmployeeId(String employeeId) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("status", Operator.EQ, "ACTIVE")
                .build();
        return findByQuery(query);
    }

    @Override
    public List<LegalDeduction> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(LegalDeduction deduction) {
        LegalDeductionPO po = toPO(deduction);
        Optional<LegalDeductionPO> existing = super.findById(po.getDeductionId());

        if (existing.isPresent()) {
            po.setCreatedAt(existing.get().getCreatedAt());
            po.setUpdatedAt(LocalDateTime.now());
            super.update(po);
        } else {
            po.setCreatedAt(LocalDateTime.now());
            po.setUpdatedAt(LocalDateTime.now());
            super.save(po);
        }
    }

    private LegalDeduction toDomain(LegalDeductionPO po) {
        return LegalDeduction.reconstitute(
                new DeductionId(po.getDeductionId()),
                po.getEmployeeId(),
                po.getCourtOrderNumber(),
                GarnishmentType.valueOf(po.getGarnishmentType()),
                po.getTotalAmount(),
                po.getDeductedAmount() != null ? po.getDeductedAmount() : BigDecimal.ZERO,
                po.getRemainingAmount(),
                po.getPriority() != null ? po.getPriority() : 1,
                po.getEffectiveDate(),
                po.getExpiryDate(),
                GarnishmentStatus.valueOf(po.getStatus()),
                po.getIssuingAuthority(),
                po.getCaseNumber(),
                po.getNote());
    }

    private LegalDeductionPO toPO(LegalDeduction deduction) {
        return LegalDeductionPO.builder()
                .deductionId(deduction.getId().getValue())
                .employeeId(deduction.getEmployeeId())
                .courtOrderNumber(deduction.getCourtOrderNumber())
                .garnishmentType(deduction.getGarnishmentType().name())
                .totalAmount(deduction.getTotalAmount())
                .deductedAmount(deduction.getDeductedAmount())
                .remainingAmount(deduction.getRemainingAmount())
                .priority(deduction.getPriority())
                .effectiveDate(deduction.getEffectiveDate())
                .expiryDate(deduction.getExpiryDate())
                .status(deduction.getStatus().name())
                .issuingAuthority(deduction.getIssuingAuthority())
                .caseNumber(deduction.getCaseNumber())
                .note(deduction.getNote())
                .build();
    }
}
