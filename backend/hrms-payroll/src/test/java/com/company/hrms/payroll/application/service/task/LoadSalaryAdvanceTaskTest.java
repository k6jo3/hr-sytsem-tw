package com.company.hrms.payroll.application.service.task;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.payroll.application.service.context.SalaryAdvanceContext;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;

/**
 * LoadSalaryAdvanceTask 單元測試
 *
 * 驗證從 Repository 載入預借薪資資料的行為
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoadSalaryAdvanceTask 測試")
class LoadSalaryAdvanceTaskTest {

    @Mock
    private ISalaryAdvanceRepository repository;

    @InjectMocks
    private LoadSalaryAdvanceTask task;

    private SalaryAdvanceContext context;

    @BeforeEach
    void setUp() {
        context = new SalaryAdvanceContext();
        context.setAdvanceId("advance-001");
    }

    @Nested
    @DisplayName("載入成功")
    class SuccessTests {

        @Test
        @DisplayName("應成功載入預借薪資並設置到 Context")
        void shouldLoadAdvanceSuccessfully() throws Exception {
            // Given
            SalaryAdvance mockAdvance = mock(SalaryAdvance.class);
            when(repository.findById(any(AdvanceId.class))).thenReturn(Optional.of(mockAdvance));

            // When
            task.execute(context);

            // Then
            assertThat(context.getSalaryAdvance()).isNotNull();
            assertThat(context.getSalaryAdvance()).isEqualTo(mockAdvance);
            verify(repository).findById(any(AdvanceId.class));
        }
    }

    @Nested
    @DisplayName("載入失敗")
    class FailureTests {

        @Test
        @DisplayName("預借記錄不存在應拋出 DomainException")
        void shouldThrowWhenAdvanceNotFound() {
            // Given
            when(repository.findById(any(AdvanceId.class))).thenReturn(Optional.empty());

            // When & Then
            DomainException ex = assertThrows(DomainException.class,
                    () -> task.execute(context));
            assertThat(ex.getErrorCode()).isEqualTo("SALARY_ADVANCE_NOT_FOUND");
            assertThat(ex.getMessage()).contains("advance-001");
        }

        @Test
        @DisplayName("advanceId 為空應拋出 IllegalArgumentException")
        void shouldThrowWhenAdvanceIdIsBlank() {
            // Given
            context.setAdvanceId("");

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> task.execute(context));
        }

        @Test
        @DisplayName("advanceId 為 null 應拋出 IllegalArgumentException")
        void shouldThrowWhenAdvanceIdIsNull() {
            // Given
            context.setAdvanceId(null);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> task.execute(context));
        }
    }

    @Test
    @DisplayName("getName 應返回 '載入預借薪資資料'")
    void shouldReturnCorrectName() {
        assertEquals("載入預借薪資資料", task.getName());
    }
}
