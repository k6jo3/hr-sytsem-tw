package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractCommandService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.PayrollRunActionRequest;
import com.company.hrms.payroll.application.dto.response.PayslipResponse;
import com.company.hrms.payroll.application.factory.PayslipDtoFactory;
import com.company.hrms.payroll.application.service.context.PayslipActionContext;
import com.company.hrms.payroll.application.service.task.FetchPayslipTask;
import com.company.hrms.payroll.application.service.task.GeneratePdfTask;

import lombok.RequiredArgsConstructor;

/**
 * 產生薪資單 PDF 服務
 * 使用 Pipeline 模式編排 PDF 產生流程
 * 
 * 外部服務整合說明：
 * - 實際 PDF 產生邏輯在 GeneratePdfTask 中
 * - 需在 hrms-document 模組完成後注入 DocumentServiceClient
 */
@Service("generatePayslipPdfServiceImpl")
@Transactional
@RequiredArgsConstructor
public class GeneratePayslipPdfServiceImpl extends AbstractCommandService<PayrollRunActionRequest, PayslipResponse> {

    private final FetchPayslipTask fetchPayslipTask;
    private final GeneratePdfTask generatePdfTask;

    @Override
    protected PayslipResponse doExecute(PayrollRunActionRequest request, JWTModel currentUser, String... args)
            throws Exception {

        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("薪資單 ID 為必填");
        }
        String payslipId = args[0];

        // 1. 建立 Context
        PayslipActionContext context = new PayslipActionContext(payslipId, currentUser, "GENERATE_PDF");

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchPayslipTask)
                .next(generatePdfTask)
                .execute();

        // 3. 回傳結果
        return PayslipDtoFactory.toResponse(context.getPayslip());
    }
}
