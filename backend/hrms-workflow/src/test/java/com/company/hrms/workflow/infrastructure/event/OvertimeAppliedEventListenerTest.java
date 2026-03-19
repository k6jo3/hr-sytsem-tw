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
 * 加班申請事件監聽器單元測試
 *
 * <p>驗證從 Kafka 收到加班申請 JSON 訊息後，
 * 正確解析並呼叫 WorkflowEventApplicationService 建立簽核流程。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OvertimeAppliedEventListener 單元測試")
class OvertimeAppliedEventListenerTest {

    @Mock
    private WorkflowEventApplicationService workflowEventApplicationService;

    /**
     * 建立含真實 ObjectMapper 的監聽器
     */
    private OvertimeAppliedEventListener createListener() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return new OvertimeAppliedEventListener(workflowEventApplicationService, objectMapper);
    }

    @Test
    @DisplayName("正確格式 JSON（含全部欄位）-> 呼叫 startWorkflowByEvent 並傳入正確參數")
    void handleOvertimeApplied_validJson_callsWorkflowService() {
        // Arrange
        OvertimeAppliedEventListener listener = createListener();
        String message = """
                {
                    "applicationId": "OT-2026-001",
                    "employeeId": "EMP-001",
                    "hours": 4.0
                }
                """;

        // Act
        listener.handleOvertimeApplied(message);

        // Assert
        verify(workflowEventApplicationService).startWorkflowByEvent(
                eq(FlowType.OVERTIME_APPROVAL),
                eq("EMP-001"),
                eq("OT-2026-001"),
                eq("OVERTIME"),
                anyString(),
                anyMap());
    }

    @Test
    @DisplayName("缺少 hours -> 仍正常建立流程（hours 為選填）")
    void handleOvertimeApplied_missingHours_stillCreatesWorkflow() {
        // Arrange
        OvertimeAppliedEventListener listener = createListener();
        String message = """
                {
                    "applicationId": "OT-2026-002",
                    "employeeId": "EMP-002"
                }
                """;

        // Act
        listener.handleOvertimeApplied(message);

        // Assert
        verify(workflowEventApplicationService).startWorkflowByEvent(
                eq(FlowType.OVERTIME_APPROVAL),
                eq("EMP-002"),
                eq("OT-2026-002"),
                eq("OVERTIME"),
                anyString(),
                anyMap());
    }

    @Test
    @DisplayName("缺少 applicationId -> 忽略事件，不建立流程")
    void handleOvertimeApplied_missingApplicationId_ignored() {
        // Arrange
        OvertimeAppliedEventListener listener = createListener();
        String message = """
                {
                    "employeeId": "EMP-003",
                    "hours": 2.0
                }
                """;

        // Act
        listener.handleOvertimeApplied(message);

        // Assert
        verify(workflowEventApplicationService, never())
                .startWorkflowByEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("缺少 employeeId -> 忽略事件，不建立流程")
    void handleOvertimeApplied_missingEmployeeId_ignored() {
        // Arrange
        OvertimeAppliedEventListener listener = createListener();
        String message = """
                {
                    "applicationId": "OT-2026-004",
                    "hours": 8.0
                }
                """;

        // Act
        listener.handleOvertimeApplied(message);

        // Assert
        verify(workflowEventApplicationService, never())
                .startWorkflowByEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("格式錯誤 JSON -> 不拋出未捕獲異常（降級處理）")
    void handleOvertimeApplied_invalidJson_doesNotThrow() {
        // Arrange
        OvertimeAppliedEventListener listener = createListener();
        String malformedMessage = "not a valid json!!!";

        // Act
        listener.handleOvertimeApplied(malformedMessage);

        // Assert
        verify(workflowEventApplicationService, never())
                .startWorkflowByEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("hours 為非數字 -> 仍正常建立流程（hours 解析失敗為 null）")
    void handleOvertimeApplied_invalidHoursFormat_stillCreatesWorkflow() {
        // Arrange
        OvertimeAppliedEventListener listener = createListener();
        String message = """
                {
                    "applicationId": "OT-2026-006",
                    "employeeId": "EMP-006",
                    "hours": "not-a-number"
                }
                """;

        // Act
        listener.handleOvertimeApplied(message);

        // Assert
        verify(workflowEventApplicationService).startWorkflowByEvent(
                eq(FlowType.OVERTIME_APPROVAL),
                eq("EMP-006"),
                eq("OT-2026-006"),
                eq("OVERTIME"),
                anyString(),
                anyMap());
    }
}
