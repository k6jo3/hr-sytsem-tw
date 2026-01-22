package com.company.hrms.workflow.domain.model.valueobject;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程邊線值物件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEdge implements Serializable {
    private String source;
    private String target;

    // 條件表達式 (僅當 source 為 CONDITION 節點時有效)
    // 例如: "totalDays > 3"
    private String condition;
}
