package com.company.hrms.payroll.application.service.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.PayslipActionContext;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 產生薪資單 PDF 任務
 * 呼叫 Document Service 產生 PDF 並更新薪資單
 * 
 * 注意：外部服務整合需在 hrms-document 模組實作後注入
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeneratePdfTask implements PipelineTask<PayslipActionContext> {

    private final IPayslipRepository payslipRepository;
    // 外部服務整合點：當 hrms-document 模組完成後，注入 DocumentServiceClient
    // private final DocumentServiceClient documentServiceClient;

    @Override
    public void execute(PayslipActionContext context) {
        // 模擬 PDF 產生 - 實際實作時呼叫 Document Service
        // String pdfUrl =
        // documentServiceClient.generatePayslipPdf(context.getPayslip());
        String pdfUrl = "/api/v1/documents/payslips/" + context.getPayslipId() + ".pdf";

        context.getPayslip().setPdfUrl(pdfUrl);
        payslipRepository.save(context.getPayslip());

        context.setPdfUrl(pdfUrl);
        log.info("已產生薪資單 PDF: {}", pdfUrl);
    }

    @Override
    public String getName() {
        return "GeneratePdfTask";
    }
}
