package com.company.hrms.document.application.service.upload.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.company.hrms.document.api.request.UploadDocumentRequest;
import com.company.hrms.document.application.service.upload.context.UploadDocumentContext;
import com.company.hrms.document.domain.service.IFileStorageService;

/**
 * SaveStorageTask 單元測試
 */
@ExtendWith(MockitoExtension.class)
class SaveStorageTaskTest {

    @Mock
    private IFileStorageService fileStorageService;

    @InjectMocks
    private SaveStorageTask task;

    @Test
    @DisplayName("執行儲存 - 應呼叫儲存服務並設定路徑至 Context")
    void execute_shouldCallStorageServiceAndSetPath() {
        // Arrange
        byte[] content = "file data".getBytes();
        UploadDocumentRequest request = UploadDocumentRequest.builder()
                .fileName("report.pdf")
                .fileContent(content)
                .build();
        UploadDocumentContext context = new UploadDocumentContext(request);

        ArgumentCaptor<String> pathCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> contentCaptor = ArgumentCaptor.forClass(byte[].class);
        when(fileStorageService.save(pathCaptor.capture(), contentCaptor.capture()))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        task.execute(context);

        // Assert
        verify(fileStorageService).save(anyString(), eq(content));

        String savedPath = pathCaptor.getValue();
        assertNotNull(savedPath);
        assertTrue(savedPath.endsWith("/report.pdf"), "路徑應以原始檔名結尾");
        assertEquals(savedPath, context.getSavedStoragePath());
    }

    @Test
    @DisplayName("執行儲存 - 路徑格式為 UUID/檔名")
    void execute_pathShouldContainUuidPrefix() {
        byte[] content = "data".getBytes();
        UploadDocumentRequest request = UploadDocumentRequest.builder()
                .fileName("doc.xlsx")
                .fileContent(content)
                .build();
        UploadDocumentContext context = new UploadDocumentContext(request);

        when(fileStorageService.save(anyString(), any(byte[].class)))
                .thenAnswer(inv -> inv.getArgument(0));

        task.execute(context);

        String path = context.getSavedStoragePath();
        // 格式：{uuid}/doc.xlsx
        String[] parts = path.split("/");
        assertEquals(2, parts.length, "路徑應為 UUID/檔名 格式");
        assertEquals("doc.xlsx", parts[1]);
        // UUID 長度為 36（含 4 個 dash）
        assertEquals(36, parts[0].length(), "UUID 部分長度應為 36");
    }
}
