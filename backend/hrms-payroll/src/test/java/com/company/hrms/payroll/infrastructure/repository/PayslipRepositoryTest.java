package com.company.hrms.payroll.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.model.entity.PayslipItem;
import com.company.hrms.payroll.domain.model.valueobject.BankAccount;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;

@DataJpaTest
@Import({ PayslipRepositoryImpl.class,
        com.company.hrms.common.infrastructure.persistence.querydsl.config.QuerydslConfig.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PayslipRepositoryTest {

    @Autowired
    private IPayslipRepository repository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        RunId runId = RunId.fromYearMonth(2025, 12);
        PayPeriod period = PayPeriod.ofMonth(2025, 12);
        LocalDate payDate = LocalDate.of(2026, 1, 5);

        Payslip payslip = Payslip.create(runId, "EMP001", "001", "John Doe", period, payDate);
        payslip.setBaseSalary(new BigDecimal("50000"));

        PayslipItem item1 = PayslipItem.createEarning("BONUS", "Bonus", new BigDecimal("5000"), "MANUAL");
        payslip.addEarningItem(item1);

        payslip.setBankAccount(new BankAccount("004", "Bank", "001", "123456", "John"));

        payslip.calculate();

        // Act
        Payslip saved = repository.save(payslip);
        Optional<Payslip> found = repository.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        Payslip loaded = found.get();

        assertThat(loaded.getId()).isEqualTo(payslip.getId());
        assertThat(loaded.getEmployeeId()).isEqualTo("EMP001");

        // Use comparesEqualTo for BigDecimal to avoid scale issues
        assertThat(loaded.getGrossWage()).isEqualByComparingTo(new BigDecimal("55000"));
        assertThat(loaded.getEarningItems()).hasSize(1);
        assertThat(loaded.getEarningItems().get(0).getItemCode()).isEqualTo("BONUS");

        // Check Bank Account
        assertThat(loaded.getBankAccount()).isNotNull();
        assertThat(loaded.getBankAccount().getBankCode()).isEqualTo("004");
        assertThat(loaded.getBankAccount().getAccountNumber()).isEqualTo("123456");
    }

    @Test
    void testFindByEmployeeId() {
        // Arrange
        RunId runId = RunId.fromYearMonth(2025, 12);
        PayPeriod period = PayPeriod.ofMonth(2025, 12);
        LocalDate payDate = LocalDate.of(2026, 1, 5);

        Payslip payslip = Payslip.create(runId, "EMP002", "002", "Jane Doe", period, payDate);
        repository.save(payslip);

        // Act
        List<Payslip> results = repository.findByEmployeeId("EMP002");

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getEmployeeName()).isEqualTo("Jane Doe");
    }
}
