package com.company.hrms.workflow.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.enums.InstanceStatus;
import com.company.hrms.workflow.domain.model.enums.TaskStatus;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;

/**
 * 流程實例 Aggregate 擴展單元測試
 * 覆蓋建立、啟動、取消、多任務審批、變數合併、事件產生
 */
class WorkflowInstanceExtendedTest {

    private WorkflowInstance instance;

    @BeforeEach
    void setUp() {
        Map<String, Object> vars = new HashMap<>();
        vars.put("leaveDays", 3);
        vars.put("leaveType", "事假");

        instance = WorkflowInstance.create(
                new WorkflowDefinitionId("DEF-LEAVE-001"),
                FlowType.LEAVE_APPROVAL,
                "emp-001",
                "LEAVE-2026-001",
                "LEAVE",
                vars);
    }

    // === 建立 ===

    @Nested
    @DisplayName("建立流程實例")
    class CreateTests {

        @Test
        @DisplayName("建立 - 初始狀態為 DRAFT")
        void create_shouldBeDraft() {
            assertEquals(InstanceStatus.DRAFT, instance.getStatus());
        }

        @Test
        @DisplayName("建立 - 正確設定所有欄位")
        void create_shouldSetAllFields() {
            assertNotNull(instance.getInstanceId());
            assertEquals("DEF-LEAVE-001", instance.getDefinitionId());
            assertEquals(FlowType.LEAVE_APPROVAL, instance.getFlowType());
            assertEquals("emp-001", instance.getApplicantId());
            assertEquals("LEAVE-2026-001", instance.getBusinessId());
            assertEquals("LEAVE", instance.getBusinessType());
            assertEquals(3, instance.getVariables().get("leaveDays"));
        }

        @Test
        @DisplayName("建立 - tasks 初始為空列表")
        void create_tasksShouldBeEmpty() {
            assertTrue(instance.getTasks().isEmpty());
        }
    }

    // === 啟動 ===

    @Nested
    @DisplayName("啟動流程")
    class StartTests {

        @Test
        @DisplayName("啟動 - 狀態轉為 RUNNING")
        void start_shouldChangeToRunning() {
            instance.start();
            assertEquals(InstanceStatus.RUNNING, instance.getStatus());
            assertNotNull(instance.getStartedAt());
        }

        @Test
        @DisplayName("啟動 - 應產生 WorkflowStartedEvent")
        void start_shouldRegisterEvent() {
            instance.start();
            assertFalse(instance.getDomainEvents().isEmpty());
        }
    }

    // === 取消 ===

    @Nested
    @DisplayName("取消流程")
    class CancelTests {

        @Test
        @DisplayName("取消 - 狀態轉為 CANCELLED")
        void cancel_shouldChangeToCancelled() {
            instance.cancel();
            assertEquals(InstanceStatus.CANCELLED, instance.getStatus());
            assertNotNull(instance.getCompletedAt());
        }

        @Test
        @DisplayName("取消 - 待處理任務應一併取消")
        void cancel_shouldCancelPendingTasks() {
            ApprovalTask task1 = createTask("TASK-001", TaskStatus.PENDING);
            ApprovalTask task2 = createTask("TASK-002", TaskStatus.APPROVED);
            instance.addTask(task1);
            instance.addTask(task2);

            instance.cancel();

            assertEquals(TaskStatus.CANCELLED, task1.getStatus());
            assertEquals(TaskStatus.APPROVED, task2.getStatus()); // 已核准的不受影響
        }

        @Test
        @DisplayName("取消 - 應產生 WorkflowCompletedEvent")
        void cancel_shouldRegisterEvent() {
            instance.cancel();
            assertFalse(instance.getDomainEvents().isEmpty());
        }
    }

    // === 核准任務 ===

    @Nested
    @DisplayName("核准任務")
    class ApproveTaskTests {

        @Test
        @DisplayName("核准 - 單一任務核准後流程完成")
        void approveTask_singleTask_shouldCompleteInstance() {
            ApprovalTask task = createTask("TASK-001", TaskStatus.PENDING);
            instance.addTask(task);

            instance.approveTask("TASK-001", "mgr-001", "同意", null);

            assertEquals(TaskStatus.APPROVED, task.getStatus());
            assertEquals(InstanceStatus.COMPLETED, instance.getStatus());
        }

        @Test
        @DisplayName("核准 - 多任務部分核准不完成流程")
        void approveTask_multipleTasksPartial_shouldNotComplete() {
            ApprovalTask task1 = createTask("TASK-001", TaskStatus.PENDING);
            ApprovalTask task2 = createTask("TASK-002", TaskStatus.PENDING);
            instance.addTask(task1);
            instance.addTask(task2);

            instance.approveTask("TASK-001", "mgr-001", "同意", null);

            assertEquals(TaskStatus.APPROVED, task1.getStatus());
            assertEquals(TaskStatus.PENDING, task2.getStatus());
            // 未全部完成，不應為 COMPLETED
            assertNotEquals(InstanceStatus.COMPLETED, instance.getStatus());
        }

        @Test
        @DisplayName("核准 - 多任務全部核准後流程完成")
        void approveTask_allTasksApproved_shouldComplete() {
            ApprovalTask task1 = createTask("TASK-001", TaskStatus.PENDING);
            ApprovalTask task2 = createTask("TASK-002", TaskStatus.PENDING);
            instance.addTask(task1);
            instance.addTask(task2);

            instance.approveTask("TASK-001", "mgr-001", "同意", null);
            instance.approveTask("TASK-002", "dir-001", "核准", null);

            assertEquals(InstanceStatus.COMPLETED, instance.getStatus());
        }

        @Test
        @DisplayName("核准 - 不存在的任務應拋出例外")
        void approveTask_notFound_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    instance.approveTask("NON-EXIST", "mgr-001", "ok", null));
        }

        @Test
        @DisplayName("核准 - 非 PENDING 狀態的任務不可核准")
        void approveTask_nonPending_shouldThrow() {
            ApprovalTask task = createTask("TASK-001", TaskStatus.APPROVED);
            instance.addTask(task);

            assertThrows(IllegalStateException.class, () ->
                    instance.approveTask("TASK-001", "mgr-001", "再核准", null));
        }

        @Test
        @DisplayName("核准 - 附帶變數應合併到流程變數")
        void approveTask_withVariables_shouldMerge() {
            ApprovalTask task = createTask("TASK-001", TaskStatus.PENDING);
            instance.addTask(task);

            Map<String, Object> newVars = new HashMap<>();
            newVars.put("mgrApproved", true);

            instance.approveTask("TASK-001", "mgr-001", "同意", newVars);

            assertTrue((Boolean) instance.getVariables().get("mgrApproved"));
            // 原有變數仍保留
            assertEquals(3, instance.getVariables().get("leaveDays"));
        }
    }

    // === 駁回任務 ===

    @Nested
    @DisplayName("駁回任務")
    class RejectTaskTests {

        @Test
        @DisplayName("駁回 - 流程狀態轉為 REJECTED")
        void rejectTask_shouldRejectInstance() {
            ApprovalTask task = createTask("TASK-001", TaskStatus.PENDING);
            instance.addTask(task);

            instance.rejectTask("TASK-001", "mgr-001", "天數太多");

            assertEquals(TaskStatus.REJECTED, task.getStatus());
            assertEquals(InstanceStatus.REJECTED, instance.getStatus());
            assertNotNull(instance.getCompletedAt());
        }

        @Test
        @DisplayName("駁回 - 其他待處理任務應取消")
        void rejectTask_shouldCancelOtherPendingTasks() {
            ApprovalTask task1 = createTask("TASK-001", TaskStatus.PENDING);
            ApprovalTask task2 = createTask("TASK-002", TaskStatus.PENDING);
            instance.addTask(task1);
            instance.addTask(task2);

            instance.rejectTask("TASK-001", "mgr-001", "不同意");

            assertEquals(TaskStatus.REJECTED, task1.getStatus());
            assertEquals(TaskStatus.CANCELLED, task2.getStatus());
        }

        @Test
        @DisplayName("駁回 - 不存在的任務應拋出例外")
        void rejectTask_notFound_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    instance.rejectTask("NON-EXIST", "mgr-001", "no"));
        }
    }

    // === updateSummary ===

    @Test
    @DisplayName("更新摘要")
    void updateSummary_shouldSetSummary() {
        assertNull(instance.getSummary());
        instance.updateSummary("事假申請 - 3天");
        assertEquals("事假申請 - 3天", instance.getSummary());
    }

    // === 輔助方法 ===

    private ApprovalTask createTask(String taskId, TaskStatus status) {
        return ApprovalTask.builder()
                .taskId(taskId)
                .instanceId(instance.getInstanceId())
                .nodeId("node-1")
                .nodeName("審核節點")
                .assigneeId("mgr-001")
                .assigneeName("主管")
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
