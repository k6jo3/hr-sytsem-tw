package com.company.hrms.insurance.infrastructure.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.insurance.application.service.withdrawal.AutoWithdrawOnTerminationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 員工離職事件監聽器單元測試
 *
 * <p>驗證從 Kafka 收到 JSON 訊息後，正確解析並呼叫退保服務。
 * 使用 Mockito 直接測試（因 @ConditionalOnProperty 不載入 bean）。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeTerminatedEventListener 單元測試")
class EmployeeTerminatedEventListenerTest {

    @Mock
    private AutoWithdrawOnTerminationService autoWithdrawService;

    @InjectMocks
    private EmployeeTerminatedEventListener listener;

    /**
     * 手動設定真實 ObjectMapper（Mockito 無法自動注入 final 或非介面欄位）
     */
    private EmployeeTerminatedEventListener createListenerWithRealMapper() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new EmployeeTerminatedEventListener(autoWithdrawService, objectMapper);
    }

    @Test
    @DisplayName("正確格式 JSON → 呼叫 withdrawAllOnTermination 並傳入正確參數")
    void handleEmployeeTerminated_validJson_callsWithdrawService() {
        // Arrange
        EmployeeTerminatedEventListener realListener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
                    "terminationDate": "2026-03-15",
                    "tenantId": "tenant-001",
                    "employeeNumber": "EMP001",
                    "fullName": "王大明"
                }
                """;

        // Act
        realListener.handleEmployeeTerminated(message);

        // Assert
        verify(autoWithdrawService).withdrawAllOnTermination(
                eq("550e8400-e29b-41d4-a716-446655440000"),
                eq(LocalDate.of(2026, 3, 15)),
                eq("tenant-001")
        );
    }

    @Test
    @DisplayName("tenantId 缺失 → 傳入 null 仍正常呼叫")
    void handleEmployeeTerminated_missingTenantId_callsWithNullTenant() {
        // Arrange
        EmployeeTerminatedEventListener realListener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "550e8400-e29b-41d4-a716-446655440001",
                    "terminationDate": "2026-06-30"
                }
                """;

        // Act
        realListener.handleEmployeeTerminated(message);

        // Assert
        verify(autoWithdrawService).withdrawAllOnTermination(
                eq("550e8400-e29b-41d4-a716-446655440001"),
                eq(LocalDate.of(2026, 6, 30)),
                eq(null)
        );
    }

    @Test
    @DisplayName("格式錯誤 JSON → 不拋出未捕獲異常（降級處理）")
    void handleEmployeeTerminated_invalidJson_doesNotThrow() {
        // Arrange
        EmployeeTerminatedEventListener realListener = createListenerWithRealMapper();
        String malformedMessage = "{ invalid json }}}";

        // Act — 不應拋出異常
        realListener.handleEmployeeTerminated(malformedMessage);

        // Assert — 退保服務不被呼叫
        verify(autoWithdrawService, never()).withdrawAllOnTermination(any(), any(), any());
    }

    @Test
    @DisplayName("日期格式錯誤 → 不拋出未捕獲異常（降級處理）")
    void handleEmployeeTerminated_invalidDate_doesNotThrow() {
        // Arrange
        EmployeeTerminatedEventListener realListener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "emp-003",
                    "terminationDate": "not-a-date"
                }
                """;

        // Act — 不應拋出異常
        realListener.handleEmployeeTerminated(message);

        // Assert — 退保服務不被呼叫
        verify(autoWithdrawService, never()).withdrawAllOnTermination(any(), any(), any());
    }
}
