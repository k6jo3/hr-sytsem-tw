package com.company.hrms.document.application.service.upload.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.upload.context.UploadDocumentContext;

@Component
public class ScanVirusTask implements PipelineTask<UploadDocumentContext> {

    @Override
    public void execute(UploadDocumentContext context) {
        // Mock virus scan
        context.setVirusFree(true);
        // In real world, call ClamAV or similar
    }
}
