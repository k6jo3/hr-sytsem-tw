package com.company.hrms.document.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.company.hrms.common.application.pipeline.PipelineExecutionException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.document.api.request.DeleteDocumentRequest;
import com.company.hrms.document.application.assembler.DocumentResponseAssembler;
import com.company.hrms.document.application.service.delete.task.CheckDeletePolicyTask;
import com.company.hrms.document.application.service.delete.task.ExecuteDeleteTask;
import com.company.hrms.document.application.service.delete.task.LoadDocumentTask;
import com.company.hrms.document.application.service.delete.task.PublishDeleteEventTask;
import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;
import com.company.hrms.document.domain.model.enums.DocumentClassification;
import com.company.hrms.document.domain.model.enums.DocumentVisibility;

@ExtendWith(MockitoExtension.class)

class DeleteDocumentServiceTest {

    private DeleteDocumentServiceImpl service;

    @Mock
    private IDocumentRepository documentRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        LoadDocumentTask loadDocumentTask = new LoadDocumentTask(documentRepository);
        CheckDeletePolicyTask checkDeletePolicyTask = new CheckDeletePolicyTask();
        ExecuteDeleteTask executeDeleteTask = new ExecuteDeleteTask(documentRepository);
        PublishDeleteEventTask publishDeleteEventTask = new PublishDeleteEventTask(eventPublisher);
        DocumentResponseAssembler assembler = new DocumentResponseAssembler();

        service = new DeleteDocumentServiceImpl(
                loadDocumentTask,
                checkDeletePolicyTask,
                executeDeleteTask,
                publishDeleteEventTask,
                assembler);
    }

    @Test
    @DisplayName("Should delete document successfully")
    void shouldDeleteDocumentSuccessfully() throws Exception {
        // Given
        String docId = UUID.randomUUID().toString();
        DeleteDocumentRequest request = new DeleteDocumentRequest(docId);

        Document document = Document.create(new DocumentId(docId), "test.pdf", "user-001");
        // 使用完成上傳狀態以避開可能的空指標或狀態限制
        document.completeUpload("/path", "application/pdf", 100L);

        when(documentRepository.findById(any(DocumentId.class))).thenReturn(Optional.of(document));
        when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        var response = service.execCommand(request, new JWTModel());

        // Then
        assertNotNull(response);
        verify(documentRepository).save(argThat(Document::isDeleted));
        verify(eventPublisher).publishEvent(any(com.company.hrms.document.domain.event.DocumentDeletedEvent.class));
    }

    @Test
    @DisplayName("Should fail when deleting a Payslip")
    void shouldFailWhenDeletingPayslip() {
        // Given
        String docId = UUID.randomUUID().toString();
        DeleteDocumentRequest request = new DeleteDocumentRequest(docId);

        // Reconstitute a PAYSLIP document
        Document document = Document.reconstitute(
                new DocumentId(docId), "payslip.pdf", "user-001",
                "PAYSLIP", "payroll", "run-001", "application/pdf", 100L, "/path",
                DocumentVisibility.PRIVATE, DocumentClassification.RESTRICTED,
                true, false, null, null, LocalDateTime.now(), LocalDateTime.now());

        when(documentRepository.findById(any(DocumentId.class))).thenReturn(Optional.of(document));

        // When & Then
        var ex = assertThrows(PipelineExecutionException.class,
                () -> service.execCommand(request, new JWTModel()));
        assertTrue(ex.getCause() instanceof IllegalStateException);
        assertEquals("Payslip cannot be deleted for audit reasons.", ex.getCause().getMessage());
    }
}
