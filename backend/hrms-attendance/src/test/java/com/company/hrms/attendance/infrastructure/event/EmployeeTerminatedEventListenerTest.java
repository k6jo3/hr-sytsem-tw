package com.company.hrms.attendance.infrastructure.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.attendance.domain.event.AnnualLeaveSettlementEvent;
import com.company.hrms.attendance.domain.model.aggregate.LeaveBalance;
import com.company.hrms.attendance.domain.model.valueobject.BalanceId;
import com.company.hrms.attendance.domain.model.valueobject.LeaveTypeId;
import com.company.hrms.attendance.domain.repository.ILeaveBalanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 離職事件監聽器單元測試
 * 驗證收到 EmployeeTerminatedEvent 後的特休結算邏輯
 */
@ExtendWith(MockitoExtension.class)
class EmployeeTerminatedEventListenerTest {

    @Mock
    private ILeaveBalanceRepository leaveBalanceRepository;

    private EmployeeTerminatedEventListener listener;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        listener = new EmployeeTerminatedEventListener(leaveBalanceRepository, objectMapper);
    }

    @Nested
    @DisplayName("handleEmployeeTerminated")
    class HandleTerminatedTests {

        @Test
        @DisplayName("員工有剩餘特休時應產生結算事件")
        void shouldProduceSettlementEvent_whenRemainingDaysExist() throws Exception {
            // 準備：員工有剩餘 5 天特休
            String employeeId = "EMP-001";
            int currentYear = LocalDate.now().getYear();
            LeaveBalance balance = LeaveBalance.reconstitute(
                new BalanceId("B1"), employeeId,
                new LeaveTypeId("ANNUAL"), currentYear,
                new BigDecimal("7"), new BigDecimal("2"),
                BigDecimal.ZERO, null
            );

            when(leaveBalanceRepository.findByEmployeeIdAndYear(eq(employeeId), eq(currentYear)))
                .thenReturn(Arrays.asList(balance));

            String message = String.format(
                "{\"employeeId\":\"%s\",\"terminationDate\":\"2026-03-17\",\"hireDate\":\"2025-03-17\"}",
                employeeId
            );

            // 執行
            AnnualLeaveSettlementEvent event = listener.handleEmployeeTerminated(message);

            // 驗證：結算事件應包含剩餘天數
            assertNotNull(event);
            assertEquals(employeeId, event.getEmployeeId());
            assertEquals(new BigDecimal("5"), event.getRemainingDays());
            assertEquals(LocalDate.of(2026, 3, 17), event.getTerminationDate());
        }

        @Test
        @DisplayName("員工無特休餘額時應返回 null")
        void shouldReturnNull_whenNoBalance() throws Exception {
            String employeeId = "EMP-002";
            int currentYear = LocalDate.now().getYear();

            when(leaveBalanceRepository.findByEmployeeIdAndYear(eq(employeeId), eq(currentYear)))
                .thenReturn(Collections.emptyList());

            String message = String.format(
                "{\"employeeId\":\"%s\",\"terminationDate\":\"2026-03-17\",\"hireDate\":\"2024-01-01\"}",
                employeeId
            );

            // 執行
            AnnualLeaveSettlementEvent event = listener.handleEmployeeTerminated(message);

            // 無餘額時不需產生結算事件
            assertNull(event);
        }

        @Test
        @DisplayName("員工剩餘天數為 0 時應返回 null")
        void shouldReturnNull_whenRemainingDaysIsZero() throws Exception {
            String employeeId = "EMP-003";
            int currentYear = LocalDate.now().getYear();
            LeaveBalance balance = LeaveBalance.reconstitute(
                new BalanceId("B2"), employeeId,
                new LeaveTypeId("ANNUAL"), currentYear,
                new BigDecimal("7"), new BigDecimal("7"),
                BigDecimal.ZERO, null
            );

            when(leaveBalanceRepository.findByEmployeeIdAndYear(eq(employeeId), eq(currentYear)))
                .thenReturn(Arrays.asList(balance));

            String message = String.format(
                "{\"employeeId\":\"%s\",\"terminationDate\":\"2026-06-30\",\"hireDate\":\"2023-01-01\"}",
                employeeId
            );

            AnnualLeaveSettlementEvent event = listener.handleEmployeeTerminated(message);
            assertNull(event);
        }

        @Test
        @DisplayName("JSON 解析失敗時應不拋出例外")
        void shouldNotThrow_whenInvalidJson() {
            assertDoesNotThrow(() ->
                listener.handleEmployeeTerminated("invalid json"));
        }
    }
}
