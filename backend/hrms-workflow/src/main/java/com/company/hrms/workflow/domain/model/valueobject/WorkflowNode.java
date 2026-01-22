package com.company.hrms.workflow.domain.model.valueobject;

import java.io.Serializable;
import java.util.Map;

import com.company.hrms.workflow.domain.model.enums.NodeType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程節點值物件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowNode implements Serializable {
    private String nodeId;
    private NodeType nodeType;
    private String name;

    // 節點配置 (如審核人類型、處理天數、條件表達式等)
    private Map<String, Object> config;

    // 便捷導覽 (非必須，取決於 Edges)
    private String nextNodeId;
    private String rejectNodeId;
}
