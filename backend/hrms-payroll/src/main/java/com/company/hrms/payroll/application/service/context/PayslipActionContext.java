package com.company.hrms.payroll.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 薪資單操作 Context
 * 用於 PDF 產生、Email 發送等操作
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PayslipActionContext extends PipelineContext {

    // === 輸入 ===
    private final String payslipId;
    private final JWTModel currentUser;
    private final String actionType;

    // === 中間資料 ===
    private Payslip payslip;

    // === 輸出 ===
    private String pdfUrl;
    private boolean emailSent;

    public PayslipActionContext(String payslipId, JWTModel currentUser, String actionType) {
        this.payslipId = payslipId;
        this.currentUser = currentUser;
        this.actionType = actionType;
    }
}
