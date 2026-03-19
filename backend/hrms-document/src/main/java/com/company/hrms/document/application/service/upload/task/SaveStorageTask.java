package com.company.hrms.document.application.service.upload.task;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.document.application.service.upload.context.UploadDocumentContext;
import com.company.hrms.document.domain.service.IFileStorageService;

import lombok.RequiredArgsConstructor;

/**
 * 將上傳的檔案寫入儲存空間
 */
@Component
@RequiredArgsConstructor
public class SaveStorageTask implements PipelineTask<UploadDocumentContext> {

    private final IFileStorageService fileStorageService;

    @Override
    public void execute(UploadDocumentContext context) {
        var req = context.getRequest();

        // 產生唯一的儲存路徑：{UUID}/{原始檔名}
        String storagePath = UUID.randomUUID() + "/" + req.getFileName();

        fileStorageService.save(storagePath, req.getFileContent());
        context.setSavedStoragePath(storagePath);
    }
}
