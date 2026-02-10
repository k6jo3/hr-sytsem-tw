package com.company.hrms.payroll.infrastructure.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.CommandBatchBaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.entity.PayslipItem;
import com.company.hrms.payroll.domain.model.valueobject.BankAccount;
import com.company.hrms.payroll.domain.model.valueobject.InsuranceDeductions;
import com.company.hrms.payroll.domain.model.valueobject.ItemType;
import com.company.hrms.payroll.domain.model.valueobject.OvertimePayDetail;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayslipId;
import com.company.hrms.payroll.domain.model.valueobject.PayslipStatus;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;
import com.company.hrms.payroll.infrastructure.po.PayslipItemPO;
import com.company.hrms.payroll.infrastructure.po.PayslipPO;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@org.springframework.transaction.annotation.Transactional
public class PayslipRepositoryImpl extends CommandBatchBaseRepository<PayslipPO, String> implements IPayslipRepository {

    public PayslipRepositoryImpl(JPAQueryFactory factory) {
        super(factory, PayslipPO.class);
    }

    @Override
    public Payslip save(Payslip payslip) {
        PayslipPO po = toPO(payslip);
        if (po.getItems() != null) {
            po.getItems().forEach(item -> item.setPayslip(po));
        }

        if (po.getPayslipId() == null || po.getPayslipId().isBlank()) {
            super.save(po);
        } else {
            em.merge(po);
        }
        return toDomain(po);
    }

    @Override
    public void saveAllPayslips(List<Payslip> payslips) {
        List<PayslipPO> pos = payslips.stream()
                .map(this::toPO)
                .collect(Collectors.toList());

        for (PayslipPO po : pos) {
            if (po.getItems() != null) {
                po.getItems().forEach(item -> item.setPayslip(po));
            }

            if (po.getPayslipId() == null || po.getPayslipId().isBlank()) {
                em.persist(po);
            } else {
                em.merge(po);
            }
        }
    }

    @Override
    public Optional<Payslip> findById(PayslipId id) {
        return super.findById(id.getValue()).map(this::toDomain);
    }

    @Override
    public List<Payslip> findByPayrollRun(RunId runId) {
        QueryGroup group = QueryBuilder.where()
                .and("runId", Operator.EQ, runId.getValue())
                .build();
        return super.findAll(group).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payslip> findByEmployeeId(String employeeId) {
        QueryGroup group = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .build();
        return super.findAll(group).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payslip> findByEmployeeAndYear(String employeeId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        QueryGroup group = QueryBuilder.where()
                .and("employeeId", Operator.EQ, employeeId)
                .and("payDate", Operator.GTE, start)
                .and("payDate", Operator.LTE, end)
                .build();
        return super.findAll(group).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public org.springframework.data.domain.Page<Payslip> findAll(QueryGroup group,
            org.springframework.data.domain.Pageable pageable) {
        return super.findPage(group, pageable).map(this::toDomain);
    }

    // ==================== Mappers ====================

    private PayslipPO toPO(Payslip domain) {
        OvertimePayDetail ot = domain.getOvertimePay();
        if (ot == null)
            ot = OvertimePayDetail.empty();

        InsuranceDeductions ins = domain.getInsuranceDeductions();
        if (ins == null)
            ins = InsuranceDeductions.empty();

        BankAccount bank = domain.getBankAccount();

        PayslipPO po = PayslipPO.builder()
                .payslipId(domain.getId().getValue())
                .runId(domain.getPayrollRunId().getValue())
                .employeeId(domain.getEmployeeId())
                .employeeCode(domain.getEmployeeNumber())
                .employeeName(domain.getEmployeeName())
                .periodStartDate(domain.getPayPeriod().getStartDate())
                .periodEndDate(domain.getPayPeriod().getEndDate())
                .payDate(domain.getPayDate())
                .baseSalary(domain.getBaseSalary())
                .grossWage(domain.getGrossWage())
                .netWage(domain.getNetWage())
                .incomeTax(domain.getIncomeTax())
                .leaveDeduction(domain.getLeaveDeduction())

                // Overtime
                .overtimePayTotal(ot.getTotal())
                .otWeekdayHours(ot.getWeekdayHours())
                .otWeekdayPay(ot.getWeekdayPay())
                .otRestDayHours(ot.getRestDayHours())
                .otRestDayPay(ot.getRestDayPay())
                .otHolidayHours(ot.getHolidayHours())
                .otHolidayPay(ot.getHolidayPay())

                // Insurance
                .insLaborFee(ins.getLaborInsurance())
                .insHealthFee(ins.getHealthInsurance())
                .insPensionFee(ins.getPensionSelfContribution())
                .insSupplementaryFee(ins.getSupplementaryPremium())

                // Bank
                .bankCode(bank != null ? bank.getBankCode() : null)
                .bankAccountNumber(bank != null ? bank.getAccountNumber() : null)

                .status(domain.getStatus().name())
                .pdfUrl(domain.getPdfUrl())
                .emailSentAt(domain.getEmailSentAt())
                .items(new ArrayList<>())
                .createdAt(domain.getCreatedAt())
                .build();

        // Items
        List<PayslipItemPO> itemPOs = new ArrayList<>();
        if (domain.getEarningItems() != null) {
            itemPOs.addAll(domain.getEarningItems().stream().map(this::toItemPO).collect(Collectors.toList()));
        }
        if (domain.getDeductionItems() != null) {
            itemPOs.addAll(domain.getDeductionItems().stream().map(this::toItemPO).collect(Collectors.toList()));
        }
        po.setItems(itemPOs);

        return po;
    }

    private PayslipItemPO toItemPO(PayslipItem domain) {
        return PayslipItemPO.builder()
                .itemId(domain.getItemId())
                .code(domain.getItemCode())
                .name(domain.getItemName())
                .type(domain.getItemType().name())
                .amount(domain.getAmount())
                .source(domain.getSource())
                .taxable(domain.isTaxable())
                .insurable(domain.isInsurable())
                .displayOrder(domain.getDisplayOrder())
                .build();
    }

    private Payslip toDomain(PayslipPO po) {
        OvertimePayDetail ot = OvertimePayDetail.builder()
                .weekdayHours(po.getOtWeekdayHours() != null ? po.getOtWeekdayHours() : java.math.BigDecimal.ZERO)
                .weekdayPay(po.getOtWeekdayPay() != null ? po.getOtWeekdayPay() : java.math.BigDecimal.ZERO)
                .restDayHours(po.getOtRestDayHours() != null ? po.getOtRestDayHours() : java.math.BigDecimal.ZERO)
                .restDayPay(po.getOtRestDayPay() != null ? po.getOtRestDayPay() : java.math.BigDecimal.ZERO)
                .holidayHours(po.getOtHolidayHours() != null ? po.getOtHolidayHours() : java.math.BigDecimal.ZERO)
                .holidayPay(po.getOtHolidayPay() != null ? po.getOtHolidayPay() : java.math.BigDecimal.ZERO)
                .build();

        InsuranceDeductions ins = InsuranceDeductions.builder()
                .laborInsurance(po.getInsLaborFee() != null ? po.getInsLaborFee() : java.math.BigDecimal.ZERO)
                .healthInsurance(po.getInsHealthFee() != null ? po.getInsHealthFee() : java.math.BigDecimal.ZERO)
                .pensionSelfContribution(
                        po.getInsPensionFee() != null ? po.getInsPensionFee() : java.math.BigDecimal.ZERO)
                .supplementaryPremium(
                        po.getInsSupplementaryFee() != null ? po.getInsSupplementaryFee() : java.math.BigDecimal.ZERO)
                .build();

        BankAccount bank = null;
        if (po.getBankCode() != null && po.getBankAccountNumber() != null) {
            try {
                bank = new BankAccount(po.getBankCode(), null, null, po.getBankAccountNumber(), null);
            } catch (Exception ignored) {
            }
        }

        List<PayslipItem> earningItems = new ArrayList<>();
        List<PayslipItem> deductionItems = new ArrayList<>();

        if (po.getItems() != null) {
            for (PayslipItemPO itemPO : po.getItems()) {
                PayslipItem item = toItemDomain(itemPO);
                if (item.isEarning()) {
                    earningItems.add(item);
                } else {
                    deductionItems.add(item);
                }
            }
        }

        return Payslip.reconstruct(
                new PayslipId(po.getPayslipId()),
                new RunId(po.getRunId()),
                po.getEmployeeId(),
                po.getEmployeeCode(),
                po.getEmployeeName(),
                new PayPeriod(po.getPeriodStartDate(), po.getPeriodEndDate()),
                po.getPayDate(),
                po.getBaseSalary() != null ? po.getBaseSalary() : java.math.BigDecimal.ZERO,
                earningItems,
                deductionItems,
                ot,
                po.getLeaveDeduction() != null ? po.getLeaveDeduction() : java.math.BigDecimal.ZERO,
                ins,
                po.getIncomeTax() != null ? po.getIncomeTax() : java.math.BigDecimal.ZERO,
                po.getGrossWage() != null ? po.getGrossWage() : java.math.BigDecimal.ZERO,
                po.getNetWage() != null ? po.getNetWage() : java.math.BigDecimal.ZERO,
                bank,
                po.getStatus() != null ? PayslipStatus.valueOf(po.getStatus()) : PayslipStatus.DRAFT,
                po.getPdfUrl(),
                po.getEmailSentAt(),
                po.getCreatedAt());
    }

    private PayslipItem toItemDomain(PayslipItemPO po) {
        return PayslipItem.reconstruct(
                po.getItemId(),
                po.getCode(),
                po.getName(),
                ItemType.valueOf(po.getType()),
                po.getAmount(),
                po.getSource(),
                po.isTaxable(),
                po.isInsurable(),
                po.getDisplayOrder() != null ? po.getDisplayOrder() : 0);
    }
}
