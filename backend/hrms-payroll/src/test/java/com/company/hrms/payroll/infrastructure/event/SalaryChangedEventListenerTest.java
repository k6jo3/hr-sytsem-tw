package com.company.hrms.payroll.infrastructure.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 員工調薪事件監聽器單元測試
 *
 * <p>驗證從 Kafka 收到 Organization.EmployeeSalaryChangedEvent JSON 訊息後，
 * 正確解析並呼叫薪資結構更新服務。
 * 使用 Mockito 直接測試（因 @ConditionalOnProperty 不載入 bean）。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SalaryChangedEventListener 單元測試")
class SalaryChangedEventListenerTest {

    @Mock
    private SalaryChangedEventListener.SalaryStructureEventHandler eventHandler;

    /**
     * 建立含真實 ObjectMapper 的監聽器
     */
    private SalaryChangedEventListener createListenerWithRealMapper() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new SalaryChangedEventListener(eventHandler, objectMapper);
    }

    @Test
    @DisplayName("正確格式 JSON → 呼叫 updateSalaryFromEvent 並傳入正確參數")
    void handleSalaryChanged_validJson_callsEventHandler() {
        // Arrange
        SalaryChangedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
                    "employeeNumber": "EMP001",
                    "fullName": "王大明",
                    "newSalary": "85000",
                    "effectiveDate": "2026-04-01",
                    "reason": "年度調薪"
                }
                """;

        // Act
        listener.handleSalaryChanged(message);

        // Assert
        verify(eventHandler).updateSalaryFromEvent(
                eq("550e8400-e29b-41d4-a716-446655440000"),
                eq(new BigDecimal("85000")),
                eq(LocalDate.of(2026, 4, 1)),
                eq("年度調薪")
        );
    }

    @Test
    @DisplayName("reason 缺失 → 傳入 null 仍正常呼叫")
    void handleSalaryChanged_missingReason_callsWithNullReason() {
        // Arrange
        SalaryChangedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "550e8400-e29b-41d4-a716-446655440001",
                    "newSalary": "92000",
                    "effectiveDate": "2026-07-01"
                }
                """;

        // Act
        listener.handleSalaryChanged(message);

        // Assert
        verify(eventHandler).updateSalaryFromEvent(
                eq("550e8400-e29b-41d4-a716-446655440001"),
                eq(new BigDecimal("92000")),
                eq(LocalDate.of(2026, 7, 1)),
                eq(null)
        );
    }

    @Test
    @DisplayName("格式錯誤 JSON → 不拋出未捕獲異常（降級處理）")
    void handleSalaryChanged_invalidJson_doesNotThrow() {
        // Arrange
        SalaryChangedEventListener listener = createListenerWithRealMapper();
        String malformedMessage = "{ invalid json }}}";

        // Act — 不應拋出異常
        listener.handleSalaryChanged(malformedMessage);

        // Assert — 事件處理器不被呼叫
        verify(eventHandler, never()).updateSalaryFromEvent(any(), any(), any(), any());
    }

    @Test
    @DisplayName("日期格式錯誤 → 不拋出未捕獲異常（降級處理）")
    void handleSalaryChanged_invalidDate_doesNotThrow() {
        // Arrange
        SalaryChangedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-003",
                    "newSalary": "85000",
                    "effectiveDate": "not-a-date"
                }
                """;

        // Act — 不應拋出異常
        listener.handleSalaryChanged(message);

        // Assert — 事件處理器不被呼叫
        verify(eventHandler, never()).updateSalaryFromEvent(any(), any(), any(), any());
    }

    @Test
    @DisplayName("薪資為非數字 → 不拋出未捕獲異常（降級處理）")
    void handleSalaryChanged_invalidSalary_doesNotThrow() {
        // Arrange
        SalaryChangedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-004",
                    "newSalary": "not-a-number",
                    "effectiveDate": "2026-04-01"
                }
                """;

        // Act — 不應拋出異常
        listener.handleSalaryChanged(message);

        // Assert — 事件處理器不被呼叫
        verify(eventHandler, never()).updateSalaryFromEvent(any(), any(), any(), any());
    }
}
