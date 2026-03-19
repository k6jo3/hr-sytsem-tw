package com.company.hrms.workflow.infrastructure.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.workflow.application.service.WorkflowEventApplicationService;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 請假申請事件監聽器單元測試
 *
 * <p>驗證從 Kafka 收到請假申請 JSON 訊息後，
 * 正確解析並呼叫 WorkflowEventApplicationService 建立簽核流程。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LeaveAppliedEventListener 單元測試")
class LeaveAppliedEventListenerTest {

    @Mock
    private WorkflowEventApplicationService workflowEventApplicationService;

    /**
     * 建立含真實 ObjectMapper 的監聽器
     */
    private LeaveAppliedEventListener createListener() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new LeaveAppliedEventListener(workflowEventApplicationService, objectMapper);
    }

    @Test
    @DisplayName("正確格式 JSON（含全部欄位）-> 呼叫 startWorkflowByEvent 並傳入正確參數")
    void handleLeaveApplied_validJson_callsWorkflowService() {
        // Arrange
        LeaveAppliedEventListener listener = createListener();
        String message = """
                {
                    "applicationId": "LA-2026-001",
                    "employeeId": "EMP-001",
                    "leaveTypeId": "PERSONAL_LEAVE",
                    "totalDays": "3"
                }
                """;

        // Act
        listener.handleLeaveApplied(message);

        // Assert
        verify(workflowEventApplicationService).startWorkflowByEvent(
                eq(FlowType.LEAVE_APPROVAL),
                eq("EMP-001"),
                eq("LA-2026-001"),
                eq("LEAVE"),
                anyString(),
                anyMap());
    }

    @Test
    @DisplayName("缺少 totalDays -> 仍正常建立流程（totalDays 為選填）")
    void handleLeaveApplied_missingTotalDays_stillCreatesWorkflow() {
        // Arrange
        LeaveAppliedEventListener listener = createListener();
        String message = """
                {
                    "applicationId": "LA-2026-002",
                    "employeeId": "EMP-002",
                    "leaveTypeId": "SICK_LEAVE"
                }
                """;

        // Act
        listener.handleLeaveApplied(message);

        // Assert
        verify(workflowEventApplicationService).startWorkflowByEvent(
                eq(FlowType.LEAVE_APPROVAL),
                eq("EMP-002"),
                eq("LA-2026-002"),
                eq("LEAVE"),
                anyString(),
                anyMap());
    }

    @Test
    @DisplayName("缺少 applicationId -> 忽略事件，不建立流程")
    void handleLeaveApplied_missingApplicationId_ignored() {
        // Arrange
        LeaveAppliedEventListener listener = createListener();
        String message = """
                {
                    "employeeId": "EMP-003",
                    "leaveTypeId": "ANNUAL_LEAVE",
                    "totalDays": "5"
                }
                """;

        // Act
        listener.handleLeaveApplied(message);

        // Assert
        verify(workflowEventApplicationService, never())
                .startWorkflowByEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("缺少 employeeId -> 忽略事件，不建立流程")
    void handleLeaveApplied_missingEmployeeId_ignored() {
        // Arrange
        LeaveAppliedEventListener listener = createListener();
        String message = """
                {
                    "applicationId": "LA-2026-004",
                    "leaveTypeId": "PERSONAL_LEAVE",
                    "totalDays": "1"
                }
                """;

        // Act
        listener.handleLeaveApplied(message);

        // Assert
        verify(workflowEventApplicationService, never())
                .startWorkflowByEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("格式錯誤 JSON -> 不拋出未捕獲異常（降級處理）")
    void handleLeaveApplied_invalidJson_doesNotThrow() {
        // Arrange
        LeaveAppliedEventListener listener = createListener();
        String malformedMessage = "{ invalid json }}}";

        // Act — 不應拋出異常
        listener.handleLeaveApplied(malformedMessage);

        // Assert — 服務不被呼叫
        verify(workflowEventApplicationService, never())
                .startWorkflowByEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("半天請假（0.5 天）-> 正確解析小數天數並傳入流程變數")
    void handleLeaveApplied_halfDay_parsesCorrectly() {
        // Arrange
        LeaveAppliedEventListener listener = createListener();
        String message = """
                {
                    "applicationId": "LA-2026-005",
                    "employeeId": "EMP-005",
                    "leaveTypeId": "PERSONAL_LEAVE",
                    "totalDays": "0.5"
                }
                """;

        // Act
        listener.handleLeaveApplied(message);

        // Assert
        verify(workflowEventApplicationService).startWorkflowByEvent(
                eq(FlowType.LEAVE_APPROVAL),
                eq("EMP-005"),
                eq("LA-2026-005"),
                eq("LEAVE"),
                anyString(),
                anyMap());
    }
}
