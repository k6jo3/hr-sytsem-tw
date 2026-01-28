package com.company.hrms.payroll.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.infrastructure.persistence.querydsl.config.QuerydslConfig;
import com.company.hrms.payroll.domain.model.aggregate.SalaryStructure;
import com.company.hrms.payroll.domain.model.entity.SalaryItem;
import com.company.hrms.payroll.domain.model.valueobject.PayrollCycle;
import com.company.hrms.payroll.domain.repository.ISalaryStructureRepository;

@DataJpaTest
@Import({ SalaryStructureRepositoryImpl.class, QuerydslConfig.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class SalaryStructureRepositoryTest {

    @Autowired
    private ISalaryStructureRepository repository;

    @Test
    void testSaveAndFind() {
        // Arrange
        // Use factory method to create valid domain object
        SalaryStructure structure = SalaryStructure.createMonthly(
                "EMP001",
                new BigDecimal("30000"),
                PayrollCycle.MONTHLY,
                LocalDate.now());

        // Create item using factory method
        SalaryItem item = SalaryItem.createEarning("ALLOWANCE", "Allowance", new BigDecimal("2000"));
        structure.addSalaryItem(item);

        // Act
        repository.save(structure);

        // Assert: Use ID from created structure (it's generated inside factory)
        Optional<SalaryStructure> found = repository.findById(structure.getId());

        assertThat(found).isPresent();
        SalaryStructure loaded = found.get();

        assertThat(loaded.getEmployeeId()).isEqualTo("EMP001");
        assertThat(loaded.getMonthlySalary()).isEqualByComparingTo(new BigDecimal("30000"));
        assertThat(loaded.getItems()).hasSize(1);
        assertThat(loaded.getItems().get(0).getItemCode()).isEqualTo("ALLOWANCE");
    }
}
