package com.company.hrms.document.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.document.domain.model.enums.DocumentTemplateStatus;

/**
 * DocumentTemplate 擴展單元測試
 * 覆蓋狀態轉換、內容設定、reconstitute
 */
class DocumentTemplateExtendedTest {

    private DocumentTemplate createTemplate(String code, String name) {
        return DocumentTemplate.create(
                new DocumentTemplateId(UUID.randomUUID().toString()), code, name, "HR");
    }

    // === 建立 ===

    @Nested
    @DisplayName("建立範本")
    class CreateTests {

        @Test
        @DisplayName("建立範本 - 預設狀態為 ACTIVE")
        void create_shouldBeActive() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");
            assertEquals(DocumentTemplateStatus.ACTIVE, template.getStatus());
        }

        @Test
        @DisplayName("建立範本 - 正確設定 code、name、category")
        void create_shouldSetFields() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");
            assertEquals("T001", template.getCode());
            assertEquals("勞動契約", template.getName());
            assertEquals("HR", template.getCategory());
        }

        @Test
        @DisplayName("建立範本 - content 初始為 null")
        void create_contentShouldBeNull() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");
            assertNull(template.getContent());
        }
    }

    // === 狀態轉換 ===

    @Nested
    @DisplayName("狀態管理")
    class StatusTests {

        @Test
        @DisplayName("停用範本 - ACTIVE -> INACTIVE")
        void deactivate_fromActive_shouldSucceed() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");
            template.deactivate();
            assertEquals(DocumentTemplateStatus.INACTIVE, template.getStatus());
        }

        @Test
        @DisplayName("重新啟用範本 - INACTIVE -> ACTIVE")
        void activate_fromInactive_shouldSucceed() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");
            template.deactivate();
            template.activate();
            assertEquals(DocumentTemplateStatus.ACTIVE, template.getStatus());
        }

        @Test
        @DisplayName("連續停用啟用應正確切換")
        void toggleStatus_shouldWork() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");

            template.deactivate();
            assertEquals(DocumentTemplateStatus.INACTIVE, template.getStatus());

            template.activate();
            assertEquals(DocumentTemplateStatus.ACTIVE, template.getStatus());

            template.deactivate();
            assertEquals(DocumentTemplateStatus.INACTIVE, template.getStatus());
        }
    }

    // === 內容設定 ===

    @Nested
    @DisplayName("設定內容")
    class ContentTests {

        @Test
        @DisplayName("設定內容 - 成功儲存")
        void setContent_shouldPersist() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");
            String content = "<html><body>合約內容：{{employeeName}}</body></html>";
            template.setContent(content);
            assertEquals(content, template.getContent());
        }

        @Test
        @DisplayName("設定內容 - 覆蓋既有內容")
        void setContent_shouldOverwrite() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");
            template.setContent("舊內容");
            template.setContent("新內容");
            assertEquals("新內容", template.getContent());
        }

        @Test
        @DisplayName("設定內容為 null")
        void setContent_null_shouldBeNull() {
            DocumentTemplate template = createTemplate("T001", "勞動契約");
            template.setContent("some content");
            template.setContent(null);
            assertNull(template.getContent());
        }
    }

    // === reconstitute ===

    @Test
    @DisplayName("reconstitute - 正確還原所有欄位")
    void reconstitute_shouldRestoreAllFields() {
        DocumentTemplateId id = new DocumentTemplateId("tpl-001");
        DocumentTemplate template = DocumentTemplate.reconstitute(
                id, "T002", "離職證明", "<p>離職證明</p>", "HR", DocumentTemplateStatus.INACTIVE);

        assertEquals(id, template.getId());
        assertEquals("T002", template.getCode());
        assertEquals("離職證明", template.getName());
        assertEquals("<p>離職證明</p>", template.getContent());
        assertEquals("HR", template.getCategory());
        assertEquals(DocumentTemplateStatus.INACTIVE, template.getStatus());
    }
}
