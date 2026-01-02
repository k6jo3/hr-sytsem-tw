package com.company.hrms.payroll.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBatchBaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollRunStatus;
import com.company.hrms.payroll.domain.model.valueobject.PayrollStatistics;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;
import com.company.hrms.payroll.infrastructure.po.PayrollRunPO;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class PayrollRunRepositoryImpl extends CommandBatchBaseRepository<PayrollRunPO, String>
        implements IPayrollRunRepository {

    public PayrollRunRepositoryImpl(JPAQueryFactory factory) {
        super(factory, PayrollRunPO.class);
    }

    @Override
    public PayrollRun save(PayrollRun run) {
        PayrollRunPO po = toPO(run);
        super.save(po);
        return toDomain(po);
    }

    @Override
    public Optional<PayrollRun> findById(RunId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<PayrollRun> findByOrganization(String organizationId) {
        QueryGroup group = QueryBuilder.where()
                .and("organizationId", Operator.EQ, organizationId)
                .build();
        return super.findAll(group).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PayrollRun> findByOrganizationAndPeriod(String organizationId, PayPeriod payPeriod) {
        QueryGroup group = QueryBuilder.where()
                .and("organizationId", Operator.EQ, organizationId)
                .and("periodStartDate", Operator.EQ, payPeriod.getStartDate())
                .and("periodEndDate", Operator.EQ, payPeriod.getEndDate())
                .build();
        return super.findOne(group).map(this::toDomain);
    }

    @Override
    public org.springframework.data.domain.Page<PayrollRun> findAll(QueryGroup group,
            org.springframework.data.domain.Pageable pageable) {
        return super.findPage(group, pageable).map(this::toDomain);
    }

    private PayrollRunPO toPO(PayrollRun domain) {
        PayrollStatistics stats = domain.getStatistics();
        if (stats == null) {
            stats = PayrollStatistics.empty();
        }

        return PayrollRunPO.builder()
                .runId(domain.getId().getValue())
                .name(domain.getName())
                .organizationId(domain.getOrganizationId())
                .payrollSystem(domain.getPayrollSystem().name())
                .periodStartDate(domain.getPayPeriod().getStartDate())
                .periodEndDate(domain.getPayPeriod().getEndDate())
                .payDate(domain.getPayDate())
                .status(domain.getStatus().name())
                .executorId(domain.getExecutedBy())
                .executedAt(domain.getExecutedAt())
                .completedAt(domain.getCompletedAt())
                .approverId(domain.getApprovedBy())
                .approvedAt(domain.getApprovedAt())
                .submittedBy(domain.getSubmittedBy())
                .submittedAt(domain.getSubmittedAt())
                .paidAt(domain.getPaidAt())
                .bankFileUrl(domain.getBankFileUrl())
                // Statistics
                .totalEmployees(stats.getTotalEmployees())
                .processedEmployees(stats.getProcessedEmployees())
                .failedEmployees(stats.getFailedEmployees())
                .totalGrossAmount(stats.getTotalGrossAmount())
                .totalNetAmount(stats.getTotalNetAmount())
                .totalDeductions(stats.getTotalDeductions())
                .totalOvertimePay(stats.getTotalOvertimePay())

                .cancelReason(domain.getCancelReason())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    private PayrollRun toDomain(PayrollRunPO po) {
        PayrollStatistics stats = PayrollStatistics.builder()
                .totalEmployees(po.getTotalEmployees() != null ? po.getTotalEmployees() : 0)
                .processedEmployees(po.getProcessedEmployees() != null ? po.getProcessedEmployees() : 0)
                .failedEmployees(po.getFailedEmployees() != null ? po.getFailedEmployees() : 0)
                .totalGrossAmount(
                        po.getTotalGrossAmount() != null ? po.getTotalGrossAmount() : java.math.BigDecimal.ZERO)
                .totalNetAmount(po.getTotalNetAmount() != null ? po.getTotalNetAmount() : java.math.BigDecimal.ZERO)
                .totalDeductions(po.getTotalDeductions() != null ? po.getTotalDeductions() : java.math.BigDecimal.ZERO)
                .totalOvertimePay(
                        po.getTotalOvertimePay() != null ? po.getTotalOvertimePay() : java.math.BigDecimal.ZERO)
                .build();

        return PayrollRun.reconstruct(
                new RunId(po.getRunId()),
                po.getName(),
                po.getOrganizationId(),
                new PayPeriod(po.getPeriodStartDate(), po.getPeriodEndDate()),
                PayrollSystem.valueOf(po.getPayrollSystem()),
                po.getPayDate(),
                PayrollRunStatus.valueOf(po.getStatus()),
                stats,
                po.getExecutorId(),
                po.getExecutedAt(),
                po.getCompletedAt(),
                po.getSubmittedBy(),
                po.getSubmittedAt(),
                po.getApproverId(),
                po.getApprovedAt(),
                po.getPaidAt(),
                po.getBankFileUrl(),
                po.getCancelReason(),
                po.getCreatedBy(),
                po.getCreatedAt());
    }
}
