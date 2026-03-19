package com.company.hrms.workflow.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.company.hrms.workflow.application.service.context.StartWorkflowContext;
import com.company.hrms.workflow.application.service.task.CreateWorkflowInstanceTask;
import com.company.hrms.workflow.application.service.task.LoadWorkflowDefinitionTask;
import com.company.hrms.workflow.application.service.task.SaveWorkflowInstanceTask;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowInstanceId;

/**
 * WorkflowEventApplicationService 單元測試
 * 覆蓋事件觸發流程啟動的成功與失敗路徑
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WorkflowEventApplicationServiceTest {

    @Mock
    private LoadWorkflowDefinitionTask loadWorkflowDefinitionTask;

    @Mock
    private CreateWorkflowInstanceTask createWorkflowInstanceTask;

    @Mock
    private SaveWorkflowInstanceTask saveWorkflowInstanceTask;

    @InjectMocks
    private WorkflowEventApplicationService service;

    private Map<String, Object> variables;

    @BeforeEach
    void setUp() throws Exception {
        variables = new HashMap<>();
        variables.put("days", 3);

        // 預設所有 Task 的 getName 回傳非 null（Pipeline 會呼叫）
        lenient().when(loadWorkflowDefinitionTask.getName()).thenReturn("載入流程定義");
        lenient().when(createWorkflowInstanceTask.getName()).thenReturn("建立流程實例");
        lenient().when(saveWorkflowInstanceTask.getName()).thenReturn("儲存流程實例");

        // 預設所有 Task 的 shouldExecute 回傳 true
        lenient().when(loadWorkflowDefinitionTask.shouldExecute(any())).thenReturn(true);
        lenient().when(createWorkflowInstanceTask.shouldExecute(any())).thenReturn(true);
        lenient().when(saveWorkflowInstanceTask.shouldExecute(any())).thenReturn(true);
    }

    @Test
    @DisplayName("事件觸發流程 - 成功建立流程實例")
    void startWorkflowByEvent_success_shouldReturnInstanceId() throws Exception {
        // 模擬 createWorkflowInstanceTask 在 context 中設定 instance
        doAnswer(invocation -> {
            StartWorkflowContext ctx = invocation.getArgument(0);
            WorkflowInstance instance = new WorkflowInstance(new WorkflowInstanceId("INST-001"));
            ctx.setInstance(instance);
            return null;
        }).when(createWorkflowInstanceTask).execute(any(StartWorkflowContext.class));

        String instanceId = service.startWorkflowByEvent(
                FlowType.LEAVE_APPROVAL, "emp-001", "LEAVE-001", "LEAVE", "事假申請 3 天", variables);

        assertEquals("INST-001", instanceId);
        verify(loadWorkflowDefinitionTask).execute(any(StartWorkflowContext.class));
        verify(createWorkflowInstanceTask).execute(any(StartWorkflowContext.class));
        verify(saveWorkflowInstanceTask).execute(any(StartWorkflowContext.class));
    }

    @Test
    @DisplayName("事件觸發流程 - LoadDefinition 失敗應拋出 RuntimeException")
    void startWorkflowByEvent_loadFails_shouldThrow() throws Exception {
        doThrow(new RuntimeException("找不到對應的流程定義"))
                .when(loadWorkflowDefinitionTask).execute(any(StartWorkflowContext.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.startWorkflowByEvent(
                        FlowType.OVERTIME_APPROVAL, "emp-002", "OT-001", "OVERTIME", "加班申請", variables));

        assertTrue(ex.getMessage().contains("事件觸發流程建立失敗"));
    }

    @Test
    @DisplayName("事件觸發流程 - SaveInstance 失敗應拋出 RuntimeException")
    void startWorkflowByEvent_saveFails_shouldThrow() throws Exception {
        // createWorkflowInstanceTask 成功設定 instance
        doAnswer(invocation -> {
            StartWorkflowContext ctx = invocation.getArgument(0);
            WorkflowInstance instance = new WorkflowInstance(new WorkflowInstanceId("INST-002"));
            ctx.setInstance(instance);
            return null;
        }).when(createWorkflowInstanceTask).execute(any(StartWorkflowContext.class));

        doThrow(new RuntimeException("資料庫寫入失敗"))
                .when(saveWorkflowInstanceTask).execute(any(StartWorkflowContext.class));

        assertThrows(RuntimeException.class, () ->
                service.startWorkflowByEvent(
                        FlowType.LEAVE_APPROVAL, "emp-001", "LEAVE-002", "LEAVE", "請假", variables));
    }

    @Test
    @DisplayName("事件觸發流程 - variables 為 null 不影響流程建立")
    void startWorkflowByEvent_nullVariables_shouldNotFail() throws Exception {
        doAnswer(invocation -> {
            StartWorkflowContext ctx = invocation.getArgument(0);
            WorkflowInstance instance = new WorkflowInstance(new WorkflowInstanceId("INST-003"));
            ctx.setInstance(instance);
            return null;
        }).when(createWorkflowInstanceTask).execute(any(StartWorkflowContext.class));

        String instanceId = service.startWorkflowByEvent(
                FlowType.PURCHASE_APPROVAL, "emp-003", "PO-001", "PURCHASE", "採購申請", null);

        assertEquals("INST-003", instanceId);
    }
}
