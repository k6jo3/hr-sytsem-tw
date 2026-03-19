package com.company.hrms.workflow.domain.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.workflow.domain.model.enums.TaskStatus;

/**
 * 審核任務 Entity 單元測試
 * 覆蓋核准、駁回、委派/轉交、狀態限制
 */
class ApprovalTaskTest {

    private ApprovalTask createPendingTask() {
        return ApprovalTask.builder()
                .taskId("TASK-001")
                .instanceId("INST-001")
                .nodeId("mgr-approval")
                .nodeName("主管核准")
                .assigneeId("mgr-001")
                .assigneeName("李主管")
                .status(TaskStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // === 核准 ===

    @Nested
    @DisplayName("核准任務")
    class ApproveTests {

        @Test
        @DisplayName("核准 - 從 PENDING 成功核准")
        void approve_fromPending_shouldSucceed() {
            ApprovalTask task = createPendingTask();
            task.approve("mgr-001", "同意，內容完整");

            assertEquals(TaskStatus.APPROVED, task.getStatus());
            assertEquals("mgr-001", task.getApproverId());
            assertEquals("同意，內容完整", task.getComment());
            assertNotNull(task.getApprovedAt());
        }

        @Test
        @DisplayName("核准 - 已核准的任務不可再次核准")
        void approve_fromApproved_shouldThrow() {
            ApprovalTask task = createPendingTask();
            task.approve("mgr-001", "同意");

            assertThrows(IllegalStateException.class, () ->
                    task.approve("mgr-002", "再次核准"));
        }

        @Test
        @DisplayName("核准 - 已駁回的任務不可核准")
        void approve_fromRejected_shouldThrow() {
            ApprovalTask task = createPendingTask();
            task.reject("mgr-001", "資料不足");

            assertThrows(IllegalStateException.class, () ->
                    task.approve("mgr-001", "改同意"));
        }

        @Test
        @DisplayName("核准 - 已取消的任務不可核准")
        void approve_fromCancelled_shouldThrow() {
            ApprovalTask task = createPendingTask();
            task.setStatus(TaskStatus.CANCELLED);

            assertThrows(IllegalStateException.class, () ->
                    task.approve("mgr-001", "同意"));
        }
    }

    // === 駁回 ===

    @Nested
    @DisplayName("駁回任務")
    class RejectTests {

        @Test
        @DisplayName("駁回 - 從 PENDING 成功駁回")
        void reject_fromPending_shouldSucceed() {
            ApprovalTask task = createPendingTask();
            task.reject("mgr-001", "請假天數過長，請修改");

            assertEquals(TaskStatus.REJECTED, task.getStatus());
            assertEquals("mgr-001", task.getApproverId());
            assertEquals("請假天數過長，請修改", task.getComment());
            assertNotNull(task.getApprovedAt());
        }

        @Test
        @DisplayName("駁回 - 已核准的任務不可駁回")
        void reject_fromApproved_shouldThrow() {
            ApprovalTask task = createPendingTask();
            task.approve("mgr-001", "同意");

            assertThrows(IllegalStateException.class, () ->
                    task.reject("mgr-001", "改為駁回"));
        }

        @Test
        @DisplayName("駁回 - 已轉交的任務不可駁回")
        void reject_fromDelegated_shouldThrow() {
            ApprovalTask task = createPendingTask();
            task.delegate("deputy-001", "陳副理", "mgr-001");

            assertThrows(IllegalStateException.class, () ->
                    task.reject("mgr-001", "駁回"));
        }
    }

    // === 委派/轉交 ===

    @Nested
    @DisplayName("委派任務")
    class DelegateTests {

        @Test
        @DisplayName("委派 - 由任務負責人成功轉交")
        void delegate_byAssignee_shouldSucceed() {
            ApprovalTask task = createPendingTask();
            task.delegate("deputy-001", "陳副理", "mgr-001");

            assertEquals(TaskStatus.DELEGATED, task.getStatus());
            assertEquals("deputy-001", task.getDelegatedToId());
            assertEquals("陳副理", task.getDelegatedToName());
        }

        @Test
        @DisplayName("委派 - 非任務負責人不可轉交")
        void delegate_byNonAssignee_shouldThrow() {
            ApprovalTask task = createPendingTask();

            assertThrows(SecurityException.class, () ->
                    task.delegate("deputy-001", "陳副理", "other-user"));
        }

        @Test
        @DisplayName("委派 - 已核准的任務不可轉交")
        void delegate_fromApproved_shouldThrow() {
            ApprovalTask task = createPendingTask();
            task.approve("mgr-001", "同意");

            assertThrows(IllegalStateException.class, () ->
                    task.delegate("deputy-001", "陳副理", "mgr-001"));
        }

        @Test
        @DisplayName("委派 - 已駁回的任務不可轉交")
        void delegate_fromRejected_shouldThrow() {
            ApprovalTask task = createPendingTask();
            task.reject("mgr-001", "不同意");

            assertThrows(IllegalStateException.class, () ->
                    task.delegate("deputy-001", "陳副理", "mgr-001"));
        }
    }

    // === 逾期 ===

    @Test
    @DisplayName("逾期標記 - 可設定與讀取")
    void overdue_shouldBeSettable() {
        ApprovalTask task = createPendingTask();
        assertFalse(task.isOverdue());

        task.setOverdue(true);
        assertTrue(task.isOverdue());
    }

    // === 到期日 ===

    @Test
    @DisplayName("到期日 - 可設定與讀取")
    void dueDate_shouldBeSettable() {
        ApprovalTask task = createPendingTask();
        assertNull(task.getDueDate());

        LocalDateTime due = LocalDateTime.of(2026, 3, 25, 17, 0);
        task.setDueDate(due);
        assertEquals(due, task.getDueDate());
    }
}
