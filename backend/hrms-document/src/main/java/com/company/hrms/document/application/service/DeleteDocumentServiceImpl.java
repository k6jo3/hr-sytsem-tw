package com.company.hrms.document.application.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.document.api.request.DeleteDocumentRequest;
import com.company.hrms.document.api.response.DocumentResponse;
import com.company.hrms.document.application.assembler.DocumentResponseAssembler;
import com.company.hrms.document.application.service.delete.context.DeleteDocumentContext;
import com.company.hrms.document.application.service.delete.task.CheckDeletePolicyTask;
import com.company.hrms.document.application.service.delete.task.ExecuteDeleteTask;
import com.company.hrms.document.application.service.delete.task.LoadDocumentTask;
import com.company.hrms.document.application.service.delete.task.PublishDeleteEventTask;

import lombok.RequiredArgsConstructor;

/**
 * 刪除文件服務實作
 */
@Service("deleteDocumentServiceImpl")
@RequiredArgsConstructor
@Transactional
public class DeleteDocumentServiceImpl implements CommandApiService<DeleteDocumentRequest, DocumentResponse> {

    @Qualifier("deleteLoadDocumentTask")
    private final LoadDocumentTask loadDocumentTask;

    private final CheckDeletePolicyTask checkDeletePolicyTask;
    private final ExecuteDeleteTask executeDeleteTask;

    @Qualifier("deletePublishEventTask")
    private final PublishDeleteEventTask publishDeleteEventTask;

    private final DocumentResponseAssembler responseAssembler;

    @Override
    public DocumentResponse execCommand(DeleteDocumentRequest request, JWTModel currentUser, String... args)
            throws Exception {

        DeleteDocumentContext ctx = new DeleteDocumentContext(request);

        BusinessPipeline.start(ctx)
                .next(loadDocumentTask)
                .next(checkDeletePolicyTask)
                .next(executeDeleteTask)
                .next(publishDeleteEventTask)
                .execute();

        return responseAssembler.toResponse(ctx.getDocument());
    }
}
