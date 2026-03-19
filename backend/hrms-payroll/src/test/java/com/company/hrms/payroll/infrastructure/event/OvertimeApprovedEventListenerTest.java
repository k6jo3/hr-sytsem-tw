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
 * 加班核准事件監聽器單元測試
 *
 * <p>驗證從 Kafka 收到加班核准 JSON 訊息後，
 * 正確解析並呼叫加班費記錄服務。
 * 使用 Mockito 直接測試（因 @ConditionalOnProperty 不載入 bean）。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OvertimeApprovedEventListener 單元測試")
class OvertimeApprovedEventListenerTest {

    @Mock
    private OvertimeApprovedEventListener.OvertimePayrollEventHandler eventHandler;

    /**
     * 建立含真實 ObjectMapper 的監聽器
     */
    private OvertimeApprovedEventListener createListenerWithRealMapper() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new OvertimeApprovedEventListener(eventHandler, objectMapper);
    }

    @Test
    @DisplayName("平日加班核准 → 呼叫 recordOvertimePay 並傳入正確參數")
    void handleOvertimeApproved_weekday_callsEventHandler() {
        // Arrange
        OvertimeApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
                    "applicationId": "OT-2026-001",
                    "overtimeType": "WEEKDAY",
                    "overtimeHours": "3",
                    "approvedBy": "MGR-001"
                }
                """;

        // Act
        listener.handleOvertimeApproved(message);

        // Assert
        verify(eventHandler).recordOvertimePay(
                eq("550e8400-e29b-41d4-a716-446655440000"),
                eq("OT-2026-001"),
                eq("WEEKDAY"),
                eq(new BigDecimal("3"))
        );
    }

    @Test
    @DisplayName("休息日加班核准 → 正確解析加班類型與時數")
    void handleOvertimeApproved_restDay_callsEventHandler() {
        // Arrange
        OvertimeApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-002",
                    "applicationId": "OT-2026-002",
                    "overtimeType": "REST_DAY",
                    "overtimeHours": "8",
                    "approvedBy": "MGR-002"
                }
                """;

        // Act
        listener.handleOvertimeApproved(message);

        // Assert
        verify(eventHandler).recordOvertimePay(
                eq("emp-002"),
                eq("OT-2026-002"),
                eq("REST_DAY"),
                eq(new BigDecimal("8"))
        );
    }

    @Test
    @DisplayName("國定假日加班核准 → 正確解析")
    void handleOvertimeApproved_holiday_callsEventHandler() {
        // Arrange
        OvertimeApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-003",
                    "applicationId": "OT-2026-003",
                    "overtimeType": "HOLIDAY",
                    "overtimeHours": "4.5",
                    "approvedBy": "MGR-001"
                }
                """;

        // Act
        listener.handleOvertimeApproved(message);

        // Assert
        verify(eventHandler).recordOvertimePay(
                eq("emp-003"),
                eq("OT-2026-003"),
                eq("HOLIDAY"),
                eq(new BigDecimal("4.5"))
        );
    }

    @Test
    @DisplayName("overtimeType 缺失 → 傳入 null 仍正常呼叫")
    void handleOvertimeApproved_missingType_callsWithNullType() {
        // Arrange
        OvertimeApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-004",
                    "applicationId": "OT-2026-004",
                    "overtimeHours": "2"
                }
                """;

        // Act
        listener.handleOvertimeApproved(message);

        // Assert
        verify(eventHandler).recordOvertimePay(
                eq("emp-004"),
                eq("OT-2026-004"),
                eq(null),
                eq(new BigDecimal("2"))
        );
    }

    @Test
    @DisplayName("格式錯誤 JSON → 不拋出未捕獲異常（降級處理）")
    void handleOvertimeApproved_invalidJson_doesNotThrow() {
        // Arrange
        OvertimeApprovedEventListener listener = createListenerWithRealMapper();
        String malformedMessage = "{ invalid json }}}";

        // Act — 不應拋出異常
        listener.handleOvertimeApproved(malformedMessage);

        // Assert — 事件處理器不被呼叫
        verify(eventHandler, never()).recordOvertimePay(any(), any(), any(), any());
    }

    @Test
    @DisplayName("時數為非數字 → overtimeHours 為 null，仍正常呼叫（降級處理）")
    void handleOvertimeApproved_invalidHoursFormat_callsWithNullHours() {
        // Arrange
        OvertimeApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-005",
                    "applicationId": "OT-2026-005",
                    "overtimeType": "WEEKDAY",
                    "overtimeHours": "abc"
                }
                """;

        // Act
        listener.handleOvertimeApproved(message);

        // Assert
        verify(eventHandler).recordOvertimePay(
                eq("emp-005"),
                eq("OT-2026-005"),
                eq("WEEKDAY"),
                eq(null)
        );
    }

    @Test
    @DisplayName("overtimeHours 缺失 → 傳入 null 仍正常呼叫")
    void handleOvertimeApproved_missingHours_callsWithNullHours() {
        // Arrange
        OvertimeApprovedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-006",
                    "applicationId": "OT-2026-006",
                    "overtimeType": "WEEKDAY"
                }
                """;

        // Act
        listener.handleOvertimeApproved(message);

        // Assert
        verify(eventHandler).recordOvertimePay(
                eq("emp-006"),
                eq("OT-2026-006"),
                eq("WEEKDAY"),
                eq(null)
        );
    }
}
