package com.company.hrms.payroll.infrastructure.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceStatus;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;
import com.company.hrms.payroll.infrastructure.po.SalaryAdvancePO;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 薪資預借 Repository 實作
 */
@Repository
public class SalaryAdvanceRepositoryImpl extends BaseRepository<SalaryAdvancePO, String>
        implements ISalaryAdvanceRepository {

    public SalaryAdvanceRepositoryImpl(JPAQueryFactory queryFactory) {
        super(queryFactory, SalaryAdvancePO.class);
    }

    @Override
    public Optional<SalaryAdvance> findById(AdvanceId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<SalaryAdvance> findByEmployeeId(String employeeId) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .build();
        return findByQuery(query);
    }

    @Override
    public List<SalaryAdvance> findActiveByEmployeeId(String employeeId) {
        QueryGroup query = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("status", Operator.IN, "DISBURSED,REPAYING")
                .build();
        return findByQuery(query);
    }

    @Override
    public List<SalaryAdvance> findByQuery(QueryGroup query) {
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(SalaryAdvance advance) {
        SalaryAdvancePO po = toPO(advance);
        Optional<SalaryAdvancePO> existing = super.findById(po.getAdvanceId());

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

    private SalaryAdvance toDomain(SalaryAdvancePO po) {
        return SalaryAdvance.reconstitute(
                new AdvanceId(po.getAdvanceId()),
                po.getEmployeeId(),
                po.getRequestedAmount(),
                po.getApprovedAmount(),
                po.getInstallmentMonths(),
                po.getInstallmentAmount(),
                po.getRepaidAmount() != null ? po.getRepaidAmount() : BigDecimal.ZERO,
                po.getRemainingBalance(),
                po.getApplicationDate(),
                po.getDisbursementDate(),
                AdvanceStatus.valueOf(po.getStatus()),
                po.getReason(),
                po.getRejectionReason(),
                po.getApproverId());
    }

    private SalaryAdvancePO toPO(SalaryAdvance advance) {
        return SalaryAdvancePO.builder()
                .advanceId(advance.getId().getValue())
                .employeeId(advance.getEmployeeId())
                .requestedAmount(advance.getRequestedAmount())
                .approvedAmount(advance.getApprovedAmount())
                .installmentMonths(advance.getInstallmentMonths())
                .installmentAmount(advance.getInstallmentAmount())
                .repaidAmount(advance.getRepaidAmount())
                .remainingBalance(advance.getRemainingBalance())
                .applicationDate(advance.getApplicationDate())
                .disbursementDate(advance.getDisbursementDate())
                .status(advance.getStatus().name())
                .reason(advance.getReason())
                .rejectionReason(advance.getRejectionReason())
                .approverId(advance.getApproverId())
                .build();
    }
}
