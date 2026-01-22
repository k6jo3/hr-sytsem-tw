package com.company.hrms.workflow.domain.model.enums;

/**
 * 流程節點類型
 */
public enum NodeType {
    START, // 開始節點
    APPROVAL, // 審核節點
    CONDITION, // 條件分流
    PARALLEL, // 平行會簽
    END // 結束節點
}
