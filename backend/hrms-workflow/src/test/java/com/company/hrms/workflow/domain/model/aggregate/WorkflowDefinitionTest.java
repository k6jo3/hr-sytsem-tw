package com.company.hrms.workflow.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.enums.NodeType;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowEdge;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowNode;

/**
 * 流程定義 Aggregate 單元測試
 * 覆蓋建立、發布、停用、版本管理
 */
class WorkflowDefinitionTest {

    private WorkflowDefinition createDefinition() {
        WorkflowNode startNode = WorkflowNode.builder()
                .nodeId("start").nodeType(NodeType.START).name("開始").build();
        WorkflowNode approvalNode = WorkflowNode.builder()
                .nodeId("mgr-approval").nodeType(NodeType.APPROVAL).name("主管核准").build();
        WorkflowNode endNode = WorkflowNode.builder()
                .nodeId("end").nodeType(NodeType.END).name("結束").build();

        WorkflowEdge edge1 = WorkflowEdge.builder().source("start").target("mgr-approval").build();
        WorkflowEdge edge2 = WorkflowEdge.builder().source("mgr-approval").target("end").build();

        return WorkflowDefinition.builder()
                .id(new WorkflowDefinitionId("DEF-001"))
                .flowName("請假簽核流程")
                .flowType(FlowType.LEAVE_APPROVAL)
                .description("標準請假簽核流程")
                .nodes(Arrays.asList(startNode, approvalNode, endNode))
                .edges(Arrays.asList(edge1, edge2))
                .status(DefinitionStatus.DRAFT)
                .version(0)
                .defaultDueDays(3)
                .createdBy("admin")
                .build();
    }

    // === 建立 ===

    @Nested
    @DisplayName("建立流程定義")
    class CreateTests {

        @Test
        @DisplayName("建立定義 - 正確設定基本欄位")
        void create_shouldSetFields() {
            WorkflowDefinition def = createDefinition();
            assertEquals("DEF-001", def.getDefinitionId());
            assertEquals("請假簽核流程", def.getFlowName());
            assertEquals(FlowType.LEAVE_APPROVAL, def.getFlowType());
            assertEquals(DefinitionStatus.DRAFT, def.getStatus());
            assertEquals(0, def.getVersion());
            assertEquals(3, def.getDefaultDueDays());
        }

        @Test
        @DisplayName("建立定義 - 含 3 個節點 2 條邊")
        void create_shouldHaveNodesAndEdges() {
            WorkflowDefinition def = createDefinition();
            assertEquals(3, def.getNodes().size());
            assertEquals(2, def.getEdges().size());
        }

        @Test
        @DisplayName("建立定義 - 使用建構子")
        void create_withConstructor_shouldWork() {
            WorkflowDefinition def = new WorkflowDefinition(new WorkflowDefinitionId("DEF-002"));
            def.setFlowName("加班簽核");
            def.setFlowType(FlowType.OVERTIME_APPROVAL);
            def.setStatus(DefinitionStatus.DRAFT);

            assertEquals("DEF-002", def.getDefinitionId());
            assertEquals("加班簽核", def.getFlowName());
        }
    }

    // === 發布 ===

    @Nested
    @DisplayName("發布流程定義")
    class PublishTests {

        @Test
        @DisplayName("發布 - DRAFT 轉為 ACTIVE，版本 +1")
        void publish_fromDraft_shouldActivateAndIncrementVersion() {
            WorkflowDefinition def = createDefinition();
            assertEquals(DefinitionStatus.DRAFT, def.getStatus());
            assertEquals(0, def.getVersion());

            def.publish();

            assertEquals(DefinitionStatus.ACTIVE, def.getStatus());
            assertEquals(1, def.getVersion());
            assertNotNull(def.getPublishedAt());
        }

        @Test
        @DisplayName("重新發布 - 版本遞增")
        void publish_twice_shouldIncrementVersionTwice() {
            WorkflowDefinition def = createDefinition();
            def.publish();
            assertEquals(1, def.getVersion());

            // 模擬修改後再次發布
            def.setStatus(DefinitionStatus.DRAFT);
            def.publish();
            assertEquals(2, def.getVersion());
        }

        @Test
        @DisplayName("發布 - version 為 null 時從 1 開始")
        void publish_nullVersion_shouldStartFromOne() {
            WorkflowDefinition def = WorkflowDefinition.builder()
                    .id(new WorkflowDefinitionId("DEF-003"))
                    .flowName("測試流程")
                    .flowType(FlowType.OTHER)
                    .status(DefinitionStatus.DRAFT)
                    .version(null)
                    .build();

            def.publish();
            assertEquals(1, def.getVersion());
        }

        @Test
        @DisplayName("發布 - publishedAt 應被設定")
        void publish_shouldSetPublishedAt() {
            WorkflowDefinition def = createDefinition();
            assertNull(def.getPublishedAt());

            def.publish();
            assertNotNull(def.getPublishedAt());
        }
    }

    // === 停用 ===

    @Nested
    @DisplayName("停用流程定義")
    class DeactivateTests {

        @Test
        @DisplayName("停用 - ACTIVE 轉為 INACTIVE")
        void deactivate_fromActive_shouldSucceed() {
            WorkflowDefinition def = createDefinition();
            def.publish();
            assertEquals(DefinitionStatus.ACTIVE, def.getStatus());

            def.deactivate();
            assertEquals(DefinitionStatus.INACTIVE, def.getStatus());
        }

        @Test
        @DisplayName("停用 - 不影響版本號")
        void deactivate_shouldNotChangeVersion() {
            WorkflowDefinition def = createDefinition();
            def.publish();
            int versionBeforeDeactivate = def.getVersion();

            def.deactivate();
            assertEquals(versionBeforeDeactivate, def.getVersion());
        }
    }

    // === setRehydratedFields ===

    @Test
    @DisplayName("setRehydratedFields - 正確還原審計欄位")
    void setRehydratedFields_shouldSetAuditFields() {
        WorkflowDefinition def = createDefinition();
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 3, 1, 14, 30);
        LocalDateTime publishedAt = LocalDateTime.of(2026, 2, 15, 9, 0);

        def.setRehydratedFields(createdAt, updatedAt, "creator", "updater", publishedAt);

        assertEquals(createdAt, def.getCreatedAt());
        assertEquals(updatedAt, def.getUpdatedAt());
        assertEquals("creator", def.getCreatedBy());
        assertEquals("updater", def.getUpdatedBy());
        assertEquals(publishedAt, def.getPublishedAt());
    }

    // === getDefinitionId ===

    @Test
    @DisplayName("getDefinitionId - 正確回傳 ID 字串")
    void getDefinitionId_shouldReturnIdValue() {
        WorkflowDefinition def = new WorkflowDefinition(new WorkflowDefinitionId("DEF-100"));
        assertEquals("DEF-100", def.getDefinitionId());
    }
}
