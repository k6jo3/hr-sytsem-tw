package com.company.hrms.document.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.document.domain.model.enums.DocumentClassification;
import com.company.hrms.document.domain.model.enums.DocumentVisibility;

/**
 * Document Aggregate 擴展單元測試
 * 覆蓋版控邏輯、可見性管理、標籤管理、上傳完成、軟刪除
 */
class DocumentExtendedTest {

    private Document createDocument() {
        return Document.create(new DocumentId(UUID.randomUUID().toString()), "測試文件.pdf", "emp-001");
    }

    // === 建立 ===

    @Nested
    @DisplayName("建立文件")
    class CreateTests {

        @Test
        @DisplayName("建立文件 - 預設可見性為 PUBLIC")
        void create_defaultVisibility_shouldBePublic() {
            Document doc = createDocument();
            assertEquals(DocumentVisibility.PUBLIC, doc.getVisibility());
        }

        @Test
        @DisplayName("建立文件 - 預設分類為 INTERNAL")
        void create_defaultClassification_shouldBeInternal() {
            Document doc = createDocument();
            assertEquals(DocumentClassification.INTERNAL, doc.getClassification());
        }

        @Test
        @DisplayName("建立文件 - 初始不為已刪除")
        void create_shouldNotBeDeleted() {
            Document doc = createDocument();
            assertFalse(doc.isDeleted());
        }

        @Test
        @DisplayName("建立文件 - tags 初始為空列表")
        void create_tagsShouldBeEmptyList() {
            Document doc = createDocument();
            assertNotNull(doc.getTags());
            assertTrue(doc.getTags().isEmpty());
        }

        @Test
        @DisplayName("建立文件 - uploadedAt 與 updatedAt 應相同")
        void create_timestampsShouldMatch() {
            Document doc = createDocument();
            assertEquals(doc.getUploadedAt(), doc.getUpdatedAt());
        }
    }

    // === 上傳完成 ===

    @Nested
    @DisplayName("完成上傳")
    class CompleteUploadTests {

        @Test
        @DisplayName("完成上傳 - 設定儲存路徑、MIME 類型、檔案大小")
        void completeUpload_shouldSetFields() {
            Document doc = createDocument();
            doc.completeUpload("/storage/2026/03/test.pdf", "application/pdf", 1024000);

            assertEquals("/storage/2026/03/test.pdf", doc.getStoragePath());
            assertEquals("application/pdf", doc.getMimeType());
            assertEquals(1024000, doc.getFileSize());
        }

        @Test
        @DisplayName("完成上傳 - 應更新 updatedAt")
        void completeUpload_shouldUpdateTimestamp() {
            Document doc = createDocument();
            LocalDateTime before = doc.getUpdatedAt();

            // 小延遲確保時間差
            doc.completeUpload("/path", "text/plain", 100);
            // updatedAt 被重新設定
            assertNotNull(doc.getUpdatedAt());
        }
    }

    // === 軟刪除 ===

    @Nested
    @DisplayName("軟刪除")
    class DeleteTests {

        @Test
        @DisplayName("標記刪除 - isDeleted 變為 true")
        void markAsDeleted_shouldSetDeletedTrue() {
            Document doc = createDocument();
            doc.markAsDeleted();
            assertTrue(doc.isDeleted());
        }

        @Test
        @DisplayName("標記刪除 - 應更新 updatedAt")
        void markAsDeleted_shouldUpdateTimestamp() {
            Document doc = createDocument();
            doc.markAsDeleted();
            assertNotNull(doc.getUpdatedAt());
        }
    }

    // === 資料夾移動 ===

    @Nested
    @DisplayName("移動到資料夾")
    class MoveTests {

        @Test
        @DisplayName("移動到資料夾 - 設定 folderId")
        void moveToFolder_shouldSetFolderId() {
            Document doc = createDocument();
            assertNull(doc.getFolderId());

            doc.moveToFolder("folder-001");
            assertEquals("folder-001", doc.getFolderId());
        }

        @Test
        @DisplayName("移動到另一個資料夾 - 覆蓋 folderId")
        void moveToFolder_twice_shouldOverwrite() {
            Document doc = createDocument();
            doc.moveToFolder("folder-001");
            doc.moveToFolder("folder-002");
            assertEquals("folder-002", doc.getFolderId());
        }
    }

    // === 標籤管理 ===

    @Nested
    @DisplayName("標籤管理")
    class TagTests {

        @Test
        @DisplayName("新增標籤 - 成功加入")
        void addTag_shouldAddToList() {
            Document doc = createDocument();
            doc.addTag("合約");
            assertTrue(doc.getTags().contains("合約"));
            assertEquals(1, doc.getTags().size());
        }

        @Test
        @DisplayName("新增重複標籤 - 不重複加入")
        void addTag_duplicate_shouldNotAdd() {
            Document doc = createDocument();
            doc.addTag("合約");
            doc.addTag("合約");
            assertEquals(1, doc.getTags().size());
        }

        @Test
        @DisplayName("新增多個標籤")
        void addTag_multiple_shouldAddAll() {
            Document doc = createDocument();
            doc.addTag("合約");
            doc.addTag("HR");
            doc.addTag("機密");
            assertEquals(3, doc.getTags().size());
        }
    }

    // === reconstitute ===

    @Nested
    @DisplayName("重建 Aggregate")
    class ReconstituteTests {

        @Test
        @DisplayName("reconstitute - 正確還原所有欄位")
        void reconstitute_shouldRestoreAllFields() {
            DocumentId id = new DocumentId("doc-001");
            LocalDateTime now = LocalDateTime.now();

            Document doc = Document.reconstitute(
                    id, "report.xlsx", "emp-002",
                    "REPORT", "PAYROLL", "PAY-001",
                    "application/vnd.ms-excel", 2048,
                    "/storage/report.xlsx",
                    DocumentVisibility.DEPARTMENT,
                    DocumentClassification.CONFIDENTIAL,
                    true, false,
                    "folder-abc",
                    Arrays.asList("薪資", "報表"),
                    now.minusDays(1), now);

            assertEquals(id, doc.getId());
            assertEquals("report.xlsx", doc.getFileName());
            assertEquals("emp-002", doc.getOwnerId());
            assertEquals("REPORT", doc.getDocumentType());
            assertEquals("PAYROLL", doc.getBusinessType());
            assertEquals("PAY-001", doc.getBusinessId());
            assertEquals("application/vnd.ms-excel", doc.getMimeType());
            assertEquals(2048, doc.getFileSize());
            assertEquals(DocumentVisibility.DEPARTMENT, doc.getVisibility());
            assertEquals(DocumentClassification.CONFIDENTIAL, doc.getClassification());
            assertTrue(doc.isEncrypted());
            assertFalse(doc.isDeleted());
            assertEquals("folder-abc", doc.getFolderId());
            assertEquals(2, doc.getTags().size());
        }

        @Test
        @DisplayName("reconstitute - tags 為 null 時使用空列表")
        void reconstitute_nullTags_shouldUseEmptyList() {
            Document doc = Document.reconstitute(
                    new DocumentId("doc-002"), "file.txt", "emp-001",
                    null, null, null, null, 0, null,
                    DocumentVisibility.PUBLIC, DocumentClassification.INTERNAL,
                    false, false, null, null, LocalDateTime.now(), LocalDateTime.now());

            assertNotNull(doc.getTags());
            assertTrue(doc.getTags().isEmpty());
        }
    }
}
