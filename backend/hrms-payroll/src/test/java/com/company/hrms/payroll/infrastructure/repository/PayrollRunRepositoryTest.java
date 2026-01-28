package com.company.hrms.payroll.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.infrastructure.persistence.querydsl.config.QuerydslConfig;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.valueobject.PayPeriod;
import com.company.hrms.payroll.domain.model.valueobject.PayrollSystem;
import com.company.hrms.payroll.domain.model.valueobject.RunId;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

@DataJpaTest
@Import({ PayrollRunRepositoryImpl.class, QuerydslConfig.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class PayrollRunRepositoryTest {

    @Autowired
    private IPayrollRunRepository repository;

    @Test
    void testSaveAndFind() {
        // Arrange
        PayPeriod period = PayPeriod.ofMonth(2025, 12);
        PayrollRun run = PayrollRun.create(
                new RunId(java.util.UUID.randomUUID().toString()),
                "Test Run",
                "ORG001", period,
                PayrollSystem.MONTHLY,
                LocalDate.of(2026, 1, 5), "USER001");

        // Act
        repository.save(run);
        Optional<PayrollRun> found = repository.findById(run.getId());

        // Assert
        assertThat(found).isPresent();
        PayrollRun loaded = found.get();
        assertThat(loaded.getOrganizationId()).isEqualTo("ORG001");
        // Status might be INITIAL or whatever create sets

        // Verify Statistics (VO)
        if (loaded.getStatistics() != null) {
            assertThat(loaded.getStatistics().getTotalEmployees()).isEqualTo(0);
        }
    }
}
