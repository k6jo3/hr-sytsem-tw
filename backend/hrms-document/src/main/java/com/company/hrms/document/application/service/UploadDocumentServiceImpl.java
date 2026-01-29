package com.company.hrms.document.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.document.api.request.UploadDocumentRequest;
import com.company.hrms.document.api.response.DocumentResponse;
import com.company.hrms.document.application.assembler.DocumentResponseAssembler;
import com.company.hrms.document.application.service.upload.context.UploadDocumentContext;
import com.company.hrms.document.application.service.upload.task.PublishEventTask;
import com.company.hrms.document.application.service.upload.task.SaveDatabaseTask;
import com.company.hrms.document.application.service.upload.task.SaveStorageTask;
import com.company.hrms.document.application.service.upload.task.ScanVirusTask;
import com.company.hrms.document.application.service.upload.task.ValidateFileTask;

import lombok.RequiredArgsConstructor;

@Service("uploadDocumentServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UploadDocumentServiceImpl implements CommandApiService<UploadDocumentRequest, DocumentResponse> {

    private final ValidateFileTask validateFileTask;
    private final ScanVirusTask scanVirusTask;
    private final SaveStorageTask saveStorageTask;
    private final SaveDatabaseTask saveDatabaseTask;
    private final PublishEventTask publishEventTask;
    private final DocumentResponseAssembler assembler;

    @Override
    public DocumentResponse execCommand(UploadDocumentRequest req, JWTModel currentUser, String... args)
            throws Exception {

        // Ensure owner is set
        if (req.getOwnerId() == null && currentUser != null) {
            req.setOwnerId(currentUser.getUserId());
        }

        UploadDocumentContext ctx = new UploadDocumentContext(req);

        BusinessPipeline.start(ctx)
                .next(validateFileTask)
                .next(scanVirusTask)
                .next(saveStorageTask)
                .next(saveDatabaseTask)
                .next(publishEventTask)
                .execute();

        return assembler.toResponse(ctx.getDocument());
    }
}
