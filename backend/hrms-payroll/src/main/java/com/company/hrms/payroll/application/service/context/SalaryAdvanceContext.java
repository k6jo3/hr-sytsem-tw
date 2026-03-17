package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.domain.model.aggregate.SalaryAdvance;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 預借薪資操作 Pipeline Context
 * 用於 Apply/Approve/Reject/Disburse/Cancel 等操作
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SalaryAdvanceContext extends PipelineContext {

    // === 輸入 ===
    /** 預借 ID（操作既有記錄時使用） */
    private String advanceId;

    /** 當前使用者 */
    private JWTModel currentUser;

    /** 操作類型 (APPLY, APPROVE, REJECT, DISBURSE, CANCEL) */
    private String actionType;

    // === 申請相關輸入 ===
    private String employeeId;
    private java.math.BigDecimal requestedAmount;
    private Integer installmentMonths;
    private String reason;

    // === 核准相關輸入 ===
    private java.math.BigDecimal approvedAmount;

    // === 駁回相關輸入 ===
    private String rejectionReason;

    // === 中間資料 ===
    private SalaryAdvance salaryAdvance;
}
