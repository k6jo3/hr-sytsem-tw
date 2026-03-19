package com.company.hrms.document.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 文件版本 Entity 單元測試
 * 覆蓋建立、版本號、欄位驗證
 */
class DocumentVersionTest {

    @Test
    @DisplayName("建立版本 - 正確設定所有欄位")
    void create_shouldSetAllFields() {
        DocumentVersion version = DocumentVersion.create(
                "doc-001", 1, "report_v1.pdf", 2048,
                "/storage/doc-001/v1/report_v1.pdf", "emp-001", "初始版本");

        assertNotNull(version.getId());
        assertEquals("doc-001", version.getDocumentId());
        assertEquals(1, version.getVersion());
        assertEquals("report_v1.pdf", version.getFileName());
        assertEquals(2048, version.getFileSize());
        assertEquals("/storage/doc-001/v1/report_v1.pdf", version.getStoragePath());
        assertEquals("emp-001", version.getUploaderId());
        assertEquals("初始版本", version.getChangeNote());
        assertNotNull(version.getUploadedAt());
    }

    @Test
    @DisplayName("建立多版本 - 版本號遞增")
    void create_multipleVersions_shouldIncrementVersion() {
        DocumentVersion v1 = DocumentVersion.create("doc-001", 1, "report.pdf", 1000,
                "/storage/v1/report.pdf", "emp-001", "初始版本");
        DocumentVersion v2 = DocumentVersion.create("doc-001", 2, "report_v2.pdf", 1500,
                "/storage/v2/report_v2.pdf", "emp-002", "修正格式");
        DocumentVersion v3 = DocumentVersion.create("doc-001", 3, "report_v3.pdf", 2000,
                "/storage/v3/report_v3.pdf", "emp-001", "新增圖表");

        assertEquals(1, v1.getVersion());
        assertEquals(2, v2.getVersion());
        assertEquals(3, v3.getVersion());

        // 同一份文件的多個版本
        assertEquals(v1.getDocumentId(), v2.getDocumentId());
        assertEquals(v2.getDocumentId(), v3.getDocumentId());
    }

    @Test
    @DisplayName("建立版本 - 不同版本有不同 ID")
    void create_differentVersions_shouldHaveDifferentIds() {
        DocumentVersion v1 = DocumentVersion.create("doc-001", 1, "file.pdf", 100, "/v1", "emp-001", null);
        DocumentVersion v2 = DocumentVersion.create("doc-001", 2, "file.pdf", 200, "/v2", "emp-001", null);

        assertNotEquals(v1.getId().getValue(), v2.getId().getValue());
    }

    @Test
    @DisplayName("建立版本 - changeNote 可為 null")
    void create_nullChangeNote_shouldSucceed() {
        DocumentVersion version = DocumentVersion.create(
                "doc-001", 1, "file.txt", 50, "/path", "emp-001", null);

        assertNull(version.getChangeNote());
    }

    @Test
    @DisplayName("建立版本 - uploadedAt 自動設定")
    void create_uploadedAtShouldBeAutoSet() {
        DocumentVersion version = DocumentVersion.create(
                "doc-001", 1, "file.txt", 50, "/path", "emp-001", "備註");

        assertNotNull(version.getUploadedAt());
    }
}
