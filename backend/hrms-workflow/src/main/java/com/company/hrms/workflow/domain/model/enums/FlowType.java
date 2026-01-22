package com.company.hrms.workflow.domain.model.enums;

/**
 * 流程類型
 * (根據需求分析書定義，可持續擴充)
 */
public enum FlowType {
    LEAVE_APPROVAL, // 請假簽核
    OVERTIME_APPROVAL, // 加班簽核
    PURCHASE_APPROVAL, // 採購簽核
    RECRUITMENT_OFFER, // 錄取通知簽核
    RESIGNATION_APPROVAL, // 離職簽核
    OTHER // 其他
}
