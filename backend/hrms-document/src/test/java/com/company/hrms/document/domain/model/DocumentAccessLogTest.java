package com.company.hrms.document.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 文件存取紀錄 Entity 單元測試
 */
class DocumentAccessLogTest {

    @Test
    @DisplayName("建立存取紀錄 - DOWNLOAD 動作")
    void create_downloadAction_shouldSetFields() {
        DocumentAccessLog log = DocumentAccessLog.create("doc-001", "emp-001", "DOWNLOAD", "192.168.1.1");

        assertNotNull(log.getId());
        assertEquals("doc-001", log.getDocumentId());
        assertEquals("emp-001", log.getUserId());
        assertEquals("DOWNLOAD", log.getAction());
        assertEquals("192.168.1.1", log.getIpAddress());
        assertNotNull(log.getAccessedAt());
    }

    @Test
    @DisplayName("建立存取紀錄 - VIEW 動作")
    void create_viewAction_shouldSucceed() {
        DocumentAccessLog log = DocumentAccessLog.create("doc-002", "emp-002", "VIEW", "10.0.0.1");

        assertEquals("VIEW", log.getAction());
        assertEquals("doc-002", log.getDocumentId());
    }

    @Test
    @DisplayName("建立存取紀錄 - DELETE 動作")
    void create_deleteAction_shouldSucceed() {
        DocumentAccessLog log = DocumentAccessLog.create("doc-003", "admin-001", "DELETE", "172.16.0.1");

        assertEquals("DELETE", log.getAction());
    }

    @Test
    @DisplayName("每次建立的 ID 應不同")
    void create_shouldGenerateUniqueIds() {
        DocumentAccessLog log1 = DocumentAccessLog.create("doc-001", "emp-001", "VIEW", "127.0.0.1");
        DocumentAccessLog log2 = DocumentAccessLog.create("doc-001", "emp-001", "VIEW", "127.0.0.1");

        assertNotEquals(log1.getId().getValue(), log2.getId().getValue());
    }
}
