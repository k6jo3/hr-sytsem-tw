package com.company.hrms.payroll.application.service.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.payroll.application.service.context.BankTransferContext;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 產生銀行薪轉檔任務
 * 彙總批次內所有薪資單，產生銀行媒體檔
 * 
 * 外部服務整合說明：
 * - 實際檔案產生邏輯需在 hrms-document 模組完成後實作
 * - 檔案格式須符合各銀行規範（如：ACH、EDIBANX）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateBankFileTask implements PipelineTask<BankTransferContext> {

    private final IPayrollRunRepository payrollRunRepository;
    // 外部服務整合點：當 hrms-document 模組完成後，注入 BankTransferFileGenerator
    // private final BankTransferFileGenerator fileGenerator;

    @Override
    public void execute(BankTransferContext context) {
        // 模擬檔案產生 - 實際實作時呼叫 Document Service
        // BankTransferFile file = fileGenerator.generate(
        // context.getPayrollRun(),
        // context.getPayslips()
        // );
        String fileUrl = "/api/v1/bank-transfers/" + UUID.randomUUID() + ".txt";

        // 更新批次的銀行檔案 URL
        context.getPayrollRun().markAsPaid(fileUrl);
        payrollRunRepository.save(context.getPayrollRun());

        context.setFileUrl(fileUrl);
        log.info("已產生銀行薪轉檔: {}, 共 {} 筆", fileUrl, context.getPayslips().size());
    }

    @Override
    public String getName() {
        return "GenerateBankFileTask";
    }
}
