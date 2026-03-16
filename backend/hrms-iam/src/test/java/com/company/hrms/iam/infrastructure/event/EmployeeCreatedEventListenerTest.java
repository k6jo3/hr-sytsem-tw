package com.company.hrms.iam.infrastructure.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.iam.application.service.user.AutoCreateUserFromEmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 員工建立事件監聽器單元測試
 *
 * <p>驗證從 Kafka 收到 JSON 訊息後，正確解析並呼叫自動建立帳號服務。
 * 使用 Mockito 直接測試（因 @ConditionalOnProperty 不載入 bean）。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeCreatedEventListener 單元測試")
class EmployeeCreatedEventListenerTest {

    @Mock
    private AutoCreateUserFromEmployeeService autoCreateUserService;

    /**
     * 建立使用真實 ObjectMapper 的 listener 實例
     */
    private EmployeeCreatedEventListener createListenerWithRealMapper() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new EmployeeCreatedEventListener(autoCreateUserService, objectMapper);
    }

    @Test
    @DisplayName("正確格式 JSON（含所有欄位）→ 呼叫自動建立帳號服務並傳入正確參數")
    void handleEmployeeCreated_validJson_callsAutoCreateService() {
        // Arrange
        EmployeeCreatedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "550e8400-e29b-41d4-a716-446655440000",
                    "employeeNumber": "E001",
                    "fullName": "王大明",
                    "companyEmail": "wang.daming@company.com",
                    "organizationId": "org-001",
                    "departmentId": "dept-001",
                    "jobTitle": "軟體工程師",
                    "hireDate": "2026-03-16"
                }
                """;

        // Act
        listener.handleEmployeeCreated(message);

        // Assert
        verify(autoCreateUserService).createUserForEmployee(argThat(dto ->
                "550e8400-e29b-41d4-a716-446655440000".equals(dto.getEmployeeId()) &&
                "E001".equals(dto.getEmployeeNumber()) &&
                "王大明".equals(dto.getFullName()) &&
                "wang.daming@company.com".equals(dto.getCompanyEmail())
        ));
    }

    @Test
    @DisplayName("companyEmail 缺失 → 仍正常呼叫（email 為 null）")
    void handleEmployeeCreated_missingEmail_callsWithNullEmail() {
        // Arrange
        EmployeeCreatedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeId": "550e8400-e29b-41d4-a716-446655440001",
                    "employeeNumber": "E002",
                    "fullName": "李小華",
                    "hireDate": "2026-03-16"
                }
                """;

        // Act
        listener.handleEmployeeCreated(message);

        // Assert
        verify(autoCreateUserService).createUserForEmployee(argThat(dto ->
                "550e8400-e29b-41d4-a716-446655440001".equals(dto.getEmployeeId()) &&
                "E002".equals(dto.getEmployeeNumber()) &&
                "李小華".equals(dto.getFullName()) &&
                dto.getCompanyEmail() == null
        ));
    }

    @Test
    @DisplayName("格式錯誤 JSON → 不拋出未捕獲異常（降級處理）")
    void handleEmployeeCreated_invalidJson_doesNotThrow() {
        // Arrange
        EmployeeCreatedEventListener listener = createListenerWithRealMapper();
        String malformedMessage = "{ invalid json }}}";

        // Act — 不應拋出異常
        listener.handleEmployeeCreated(malformedMessage);

        // Assert — 自動建立帳號服務不被呼叫
        verify(autoCreateUserService, never()).createUserForEmployee(any());
    }

    @Test
    @DisplayName("employeeId 缺失 → 不拋出未捕獲異常（降級處理）")
    void handleEmployeeCreated_missingEmployeeId_doesNotThrow() {
        // Arrange
        EmployeeCreatedEventListener listener = createListenerWithRealMapper();
        String message = """
                {
                    "employeeNumber": "E003",
                    "fullName": "張三"
                }
                """;

        // Act — 不應拋出異常
        listener.handleEmployeeCreated(message);

        // Assert — 仍會嘗試呼叫服務，但 employeeId 為 null
        verify(autoCreateUserService).createUserForEmployee(argThat(dto ->
                dto.getEmployeeId() == null
        ));
    }
}
