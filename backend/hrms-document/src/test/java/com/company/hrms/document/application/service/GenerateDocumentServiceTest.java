package com.company.hrms.document.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.document.api.request.GenerateDocumentRequest;
import com.company.hrms.document.application.assembler.DocumentResponseAssembler;
import com.company.hrms.document.application.service.generate.task.FetchEmployeeDataTask;
import com.company.hrms.document.application.service.generate.task.LoadTemplateTask;
import com.company.hrms.document.application.service.generate.task.PublishEventTask;
import com.company.hrms.document.application.service.generate.task.RenderDocumentTask;
import com.company.hrms.document.application.service.generate.task.SaveDocumentTask;
import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.DocumentTemplate;
import com.company.hrms.document.domain.model.DocumentTemplateId;
import com.company.hrms.document.domain.model.IDocumentRepository;
import com.company.hrms.document.domain.model.IDocumentTemplateRepository;

@ExtendWith(MockitoExtension.class)

class GenerateDocumentServiceTest {

        private GenerateDocumentServiceImpl service;

        @Mock
        private IDocumentTemplateRepository templateRepository;
        @Mock
        private IDocumentRepository documentRepository;
        @Mock
        private ApplicationEventPublisher eventPublisher;

        @BeforeEach
        void setUp() {
                // 手動組裝以確保測試 Task 邏輯
                LoadTemplateTask loadTemplateTask = new LoadTemplateTask(templateRepository);
                FetchEmployeeDataTask fetchEmployeeDataTask = new FetchEmployeeDataTask();
                RenderDocumentTask renderDocumentTask = new RenderDocumentTask();
                SaveDocumentTask saveDocumentTask = new SaveDocumentTask(documentRepository);
                PublishEventTask publishEventTask = new PublishEventTask(eventPublisher);
                DocumentResponseAssembler assembler = new DocumentResponseAssembler();

                service = new GenerateDocumentServiceImpl(
                                loadTemplateTask,
                                fetchEmployeeDataTask,
                                renderDocumentTask,
                                saveDocumentTask,
                                publishEventTask,
                                assembler);
        }

        @Test
        @DisplayName("Should generate document successfully")
        void shouldGenerateDocumentSuccessfully() throws Exception {
                // Given
                String templateCode = "EMPLOYMENT_CERTIFICATE";
                String employeeId = UUID.randomUUID().toString();
                GenerateDocumentRequest request = GenerateDocumentRequest.builder()
                                .templateCode(templateCode)
                                .employeeId(employeeId)
                                .build();

                DocumentTemplate template = DocumentTemplate.create(
                                new DocumentTemplateId("T001"),
                                "EMPLOYMENT_CERTIFICATE",
                                "在職證明",
                                "HR");

                when(templateRepository.findByCode(templateCode)).thenReturn(Optional.of(template));
                when(documentRepository.save(any(Document.class))).thenAnswer(invocation -> invocation.getArgument(0));

                // When
                var response = service.execCommand(request, new JWTModel());

                // Then
                assertNotNull(response);
                assertEquals(employeeId, response.getOwnerId());
                assertTrue(response.getFileName().contains("在職證明"));

                verify(templateRepository).findByCode(templateCode);
                verify(documentRepository).save(any(Document.class));
                verify(eventPublisher, times(1))
                                .publishEvent(any(com.company.hrms.document.domain.event.DocumentGeneratedEvent.class));
        }

        @Test
        @DisplayName("Should throw exception when template not found")
        void shouldThrowExceptionWhenTemplateNotFound() {
                // Given
                GenerateDocumentRequest request = GenerateDocumentRequest.builder()
                                .templateCode("UNKNOWN")
                                .employeeId("E001")
                                .build();

                when(templateRepository.findByCode("UNKNOWN")).thenReturn(Optional.empty());

                // When & Then
                var ex = assertThrows(com.company.hrms.common.application.pipeline.PipelineExecutionException.class,
                                () -> service.execCommand(request, new JWTModel()));
                assertTrue(ex.getCause() instanceof IllegalArgumentException);
                assertEquals("Template not found: UNKNOWN", ex.getCause().getMessage());
        }
}
