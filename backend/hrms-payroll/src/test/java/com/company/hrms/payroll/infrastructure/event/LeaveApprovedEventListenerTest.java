package com.company.hrms.payroll.infrastructure.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 請假核准事件監聽器單元測試
 *
 * <p>驗證從 Kafka 收到請假核准 JSON 訊息後，
 * 正確解析並呼叫薪資扣款記錄服務。
 * 使用 Mockito 直接測試（因 @ConditionalOnProperty 不載入 bean）。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LeaveApprovedEventListener 單元測試")
class LeaveApprovedEventListenerTest {

    @Mock
    private LeaveApprovedEventListener.LeavePayrollEventHandler eventHandler;

    /**
     * 建立含真實 ObjectMapper 的監聽器
     */
    private LeaveApprovedEventListener createListenerWithRealMapper() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new LeaveApprovedEventListener(eventHandler, objectMapper);
    }

    @Test
    @DisplayName("正確格式 JSON（含天數與時數）→ 呼叫 recordLeaveDeduction 並傳入正確參數")
    void handleLeaveApproved_validJsonWithDaysAndHours_callsEventHandler() {
        // Arrange
        LeaveApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
                    "applicationId": "LA-2026-001",
                    "leaveType": "PERSONAL_LEAVE",
                    "leaveDays": "2",
                    "leaveHours": "16",
                    "approvedBy": "MGR-001"
                }
                """;

        // Act
        listener.handleLeaveApproved(message);

        // Assert
        verify(eventHandler).recordLeaveDeduction(
                eq("550e8400-e29b-41d4-a716-446655440000"),
                eq("LA-2026-001"),
                eq("PERSONAL_LEAVE"),
                eq(new BigDecimal("2")),
                eq(new BigDecimal("16"))
        );
    }

    @Test
    @DisplayName("僅有天數、無時數 → leaveHours 為 null，仍正常呼叫")
    void handleLeaveApproved_onlyDays_callsWithNullHours() {
        // Arrange
        LeaveApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-002",
                    "applicationId": "LA-2026-002",
                    "leaveType": "SICK_LEAVE",
                    "leaveDays": "1"
                }
                """;

        // Act
        listener.handleLeaveApproved(message);

        // Assert
        verify(eventHandler).recordLeaveDeduction(
                eq("emp-002"),
                eq("LA-2026-002"),
                eq("SICK_LEAVE"),
                eq(new BigDecimal("1")),
                eq(null)
        );
    }

    @Test
    @DisplayName("特休假（ANNUAL_LEAVE）→ 正常記錄，由 Handler 決定是否扣薪")
    void handleLeaveApproved_annualLeave_callsEventHandler() {
        // Arrange
        LeaveApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-003",
                    "applicationId": "LA-2026-003",
                    "leaveType": "ANNUAL_LEAVE",
                    "leaveDays": "3",
                    "leaveHours": "24",
                    "approvedBy": "MGR-002"
                }
                """;

        // Act
        listener.handleLeaveApproved(message);

        // Assert
        verify(eventHandler).recordLeaveDeduction(
                eq("emp-003"),
                eq("LA-2026-003"),
                eq("ANNUAL_LEAVE"),
                eq(new BigDecimal("3")),
                eq(new BigDecimal("24"))
        );
    }

    @Test
    @DisplayName("格式錯誤 JSON → 不拋出未捕獲異常（降級處理）")
    void handleLeaveApproved_invalidJson_doesNotThrow() {
        // Arrange
        LeaveApprovedEventListener listener = createListenerWithRealMapper();
        String malformedMessage = "{ invalid json }}}";

        // Act — 不應拋出異常
        listener.handleLeaveApproved(malformedMessage);

        // Assert — 事件處理器不被呼叫
        verify(eventHandler, never()).recordLeaveDeduction(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("天數為非數字 → leaveDays 為 null，仍正常呼叫（降級處理）")
    void handleLeaveApproved_invalidDaysFormat_callsWithNullDays() {
        // Arrange
        LeaveApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-004",
                    "applicationId": "LA-2026-004",
                    "leaveType": "PERSONAL_LEAVE",
                    "leaveDays": "not-a-number",
                    "leaveHours": "8"
                }
                """;

        // Act
        listener.handleLeaveApproved(message);

        // Assert — leaveDays 解析失敗為 null，leaveHours 正常
        verify(eventHandler).recordLeaveDeduction(
                eq("emp-004"),
                eq("LA-2026-004"),
                eq("PERSONAL_LEAVE"),
                eq(null),
                eq(new BigDecimal("8"))
        );
    }

    @Test
    @DisplayName("半天請假（0.5 天）→ 正確解析小數天數")
    void handleLeaveApproved_halfDay_parsesCorrectly() {
        // Arrange
        LeaveApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-005",
                    "applicationId": "LA-2026-005",
                    "leaveType": "PERSONAL_LEAVE",
                    "leaveDays": "0.5",
                    "leaveHours": "4"
                }
                """;

        // Act
        listener.handleLeaveApproved(message);

        // Assert
        verify(eventHandler).recordLeaveDeduction(
                eq("emp-005"),
                eq("LA-2026-005"),
                eq("PERSONAL_LEAVE"),
                eq(new BigDecimal("0.5")),
                eq(new BigDecimal("4"))
        );
    }
}
