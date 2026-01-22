package com.company.hrms.workflow.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.enums.InstanceStatus;
import com.company.hrms.workflow.domain.model.enums.TaskStatus;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;

class WorkflowInstanceTest {

    private WorkflowInstance instance;
    private String taskId;

    @BeforeEach
    void setUp() {
        WorkflowDefinitionId defId = new WorkflowDefinitionId("DEF-001");
        Map<String, Object> vars = new HashMap<>();
        vars.put("amount", 1000);

        instance = WorkflowInstance.create(
                defId,
                FlowType.OTHER,
                "user01",
                "B-001",
                "LEAVE",
                vars);

        // Manually add a task for testing
        taskId = UUID.randomUUID().toString();
        ApprovalTask task = new ApprovalTask();
        task.setTaskId(taskId);
        task.setNodeId("manager");
        task.setNodeName("Manager Approval");
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());

        instance.addTask(task);
    }

    @Test
    @DisplayName("建立流程實例應為 DRAFT 狀態")
    void testCreateInstance() {
        assertNotNull(instance.getInstanceId());
        assertEquals(InstanceStatus.DRAFT, instance.getStatus());
        assertEquals("user01", instance.getApplicantId());
        assertEquals(1, instance.getTasks().size()); // setUp added one
    }

    @Test
    @DisplayName("核准任務應更新任務狀態")
    void testApproveTask() {
        // Approve
        instance.approveTask(taskId, "manager01", "Ok", null);

        // Check Task Status
        ApprovalTask task = instance.getTasks().get(0);
        assertEquals(TaskStatus.APPROVED, task.getStatus());
        assertEquals("Ok", task.getComment());
        assertEquals("manager01", task.getApproverId());

        // Check Instance runs to complete (since only 1 task)
        assertEquals(InstanceStatus.COMPLETED, instance.getStatus());
    }

    @Test
    @DisplayName("駁回任務應將流程設為 REJECTED")
    void testRejectTask() {
        // Reject
        instance.rejectTask(taskId, "manager01", "No way");

        // Check Task Status
        ApprovalTask task = instance.getTasks().get(0);
        assertEquals(TaskStatus.REJECTED, task.getStatus());
        assertEquals("No way", task.getComment());

        // Check Instance Status
        assertEquals(InstanceStatus.REJECTED, instance.getStatus());
    }
}
