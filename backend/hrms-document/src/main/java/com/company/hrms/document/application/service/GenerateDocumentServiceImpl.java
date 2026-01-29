package com.company.hrms.document.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.document.api.request.GenerateDocumentRequest;
import com.company.hrms.document.api.response.DocumentResponse;
import com.company.hrms.document.application.assembler.DocumentResponseAssembler;
import com.company.hrms.document.application.service.generate.context.GenerateDocumentContext;
import com.company.hrms.document.application.service.generate.task.FetchEmployeeDataTask;
import com.company.hrms.document.application.service.generate.task.LoadTemplateTask;
import com.company.hrms.document.application.service.generate.task.PublishEventTask;
import com.company.hrms.document.application.service.generate.task.RenderDocumentTask;
import com.company.hrms.document.application.service.generate.task.SaveDocumentTask;

import lombok.RequiredArgsConstructor;

/**
 * 產生文件服務實作
 */
@Service("generateDocumentServiceImpl")
@RequiredArgsConstructor
@Transactional
public class GenerateDocumentServiceImpl implements CommandApiService<GenerateDocumentRequest, DocumentResponse> {

    private final LoadTemplateTask loadTemplateTask;
    private final FetchEmployeeDataTask fetchEmployeeDataTask;
    private final RenderDocumentTask renderDocumentTask;
    private final SaveDocumentTask saveDocumentTask;

    @Qualifier("generatePublishEventTask")
    private final PublishEventTask publishEventTask;

    private final DocumentResponseAssembler responseAssembler;

    @Override
    public DocumentResponse execCommand(GenerateDocumentRequest request, JWTModel currentUser, String... args)
            throws Exception {

        GenerateDocumentContext ctx = new GenerateDocumentContext(request);

        BusinessPipeline.start(ctx)
                .next(loadTemplateTask)
                .next(fetchEmployeeDataTask)
                .next(renderDocumentTask)
                .next(saveDocumentTask)
                .next(publishEventTask)
                .execute();

        return responseAssembler.toResponse(ctx.getDocument());
    }
}
