package com.company.hrms.payroll.application.service.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;

/**
 * SaveSalaryAdvanceTask 單元測試
 *
 * 驗證儲存預借薪資的行為
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SaveSalaryAdvanceTask 測試")
class SaveSalaryAdvanceTaskTest {

    @Mock
    private ISalaryAdvanceRepository repository;

    @InjectMocks
    private SaveSalaryAdvanceTask task;

    private SalaryAdvanceContext context;
    private SalaryAdvance testAdvance;

    @BeforeEach
    void setUp() {
        testAdvance = new SalaryAdvance(
                AdvanceId.generate(),
                "emp-uuid-001",
                new BigDecimal("30000"),
                3,
                "家庭急需");

        context = new SalaryAdvanceContext();
        context.setSalaryAdvance(testAdvance);
    }

    @Test
    @DisplayName("應成功呼叫 repository.save() 儲存預借薪資")
    void shouldCallRepositorySave() throws Exception {
        // When
        task.execute(context);

        // Then
        verify(repository).save(testAdvance);
    }

    @Test
    @DisplayName("repository.save() 應只被呼叫一次")
    void shouldCallSaveExactlyOnce() throws Exception {
        // When
        task.execute(context);

        // Then
        verify(repository, times(1)).save(any(SalaryAdvance.class));
    }

    @Test
    @DisplayName("getName 應返回 '儲存預借薪資'")
    void shouldReturnCorrectName() {
        assertEquals("儲存預借薪資", task.getName());
    }
}
