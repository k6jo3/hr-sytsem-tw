package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
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
 * 注意：雖然這是一個 GET 請求，但因為涉及 PDF 產生與 state 更新 (pdfUrl)，
 * 在此架構下由 Query Controller 呼叫。
 */
@Service("generatePayslipPdfServiceImpl")
@Transactional
@RequiredArgsConstructor
public class GeneratePayslipPdfServiceImpl extends AbstractQueryService<String, PayslipResponse> {

    private final FetchPayslipTask fetchPayslipTask;
    private final GeneratePdfTask generatePdfTask;

    @Override
    protected QueryGroup buildQuery(String request, JWTModel currentUser) {
        return QueryBuilder.where().build();
    }

    @Override
    protected PayslipResponse executeQuery(QueryGroup query, String id, JWTModel currentUser, String... args)
            throws Exception {
        String targetId = (args != null && args.length > 0) ? args[0] : id;

        if (targetId == null) {
            throw new IllegalArgumentException("薪資單 ID 為必填");
        }

        // 1. 建立 Context
        PayslipActionContext context = new PayslipActionContext(targetId, currentUser, "GENERATE_PDF");

        // 2. 執行 Pipeline
        BusinessPipeline.start(context)
                .next(fetchPayslipTask)
                .next(generatePdfTask)
                .execute();

        // 3. 回傳結果
        return PayslipDtoFactory.toResponse(context.getPayslip());
    }
}
