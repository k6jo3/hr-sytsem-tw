package com.company.hrms.document.application.service.delete.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.delete.context.DeleteDocumentContext;

import lombok.RequiredArgsConstructor;

/**
 * 檢查刪除政策任務
 */
@Component
@RequiredArgsConstructor
public class CheckDeletePolicyTask implements PipelineTask<DeleteDocumentContext> {

    @Override
    public void execute(DeleteDocumentContext context) {
        var doc = context.getDocument();

        // 業務邏輯：薪資單不可刪除
        if ("PAYSLIP".equals(doc.getDocumentType())) {
            throw new IllegalStateException("Payslip cannot be deleted for audit reasons.");
        }

        // 這裡也可以加入權限檢查邏輯，或是放在獨立的 Task
    }
}
