package com.company.hrms.document.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DocumentTest {

    @Test
    @DisplayName("Should create document valid fields")
    void shouldCreateDocumentWithValidFields() {
        // Given
        DocumentId id = new DocumentId(UUID.randomUUID().toString());
        String fileName = "test.pdf";
        String ownerId = "emp-001";

        // When
        Document doc = Document.create(id, fileName, ownerId);

        // Then
        assertNotNull(doc);
        assertEquals(id, doc.getId());
        assertEquals(fileName, doc.getFileName());
        assertEquals(ownerId, doc.getOwnerId());
        assertNotNull(doc.getUploadedAt());
        assertFalse(doc.isDeleted());

        // New fields
        assertNotNull(doc.getUpdatedAt()); // Should be same as uploadedAt initially
        // Default values
        assertEquals("PUBLIC", doc.getVisibility().name());
        assertEquals("INTERNAL", doc.getClassification().name());
        assertTrue(doc.getTags().isEmpty());
    }

    @Test
    @DisplayName("Should update document metadata")
    void shouldUpdateDocumentMetadata() {
        DocumentId id = new DocumentId(UUID.randomUUID().toString());
        Document doc = Document.create(id, "doc.pdf", "user1");

        doc.moveToFolder("folder-123");
        doc.addTag("contract");

        assertEquals("folder-123", doc.getFolderId());
        assertTrue(doc.getTags().contains("contract"));
        assertNotNull(doc.getUpdatedAt());
    }

    @Test
    @DisplayName("Should mark document as deleted")
    void shouldMarkDocumentAsDeleted() {
        // Given
        Document doc = Document.create(new DocumentId(UUID.randomUUID().toString()), "test.doc", "emp-001");

        // When
        doc.markAsDeleted();

        // Then
        assertTrue(doc.isDeleted());
    }
}
