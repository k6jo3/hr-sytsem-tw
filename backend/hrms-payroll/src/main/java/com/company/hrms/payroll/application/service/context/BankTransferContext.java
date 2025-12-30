package com.company.hrms.payroll.application.service.context;

import java.util.ArrayList;
import java.util.List;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 銀行薪轉檔案 Context
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BankTransferContext extends PipelineContext {

    // === 輸入 ===
    private final PayrollRunActionRequest request;
    private final JWTModel currentUser;

    // === 中間資料 ===
    private PayrollRun payrollRun;
    private List<Payslip> payslips = new ArrayList<>();

    // === 輸出 ===
    private String fileUrl;

    public BankTransferContext(PayrollRunActionRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }
}
