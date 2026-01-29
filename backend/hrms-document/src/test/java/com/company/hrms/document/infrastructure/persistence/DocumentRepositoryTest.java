package com.company.hrms.document.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.document.domain.model.Document;
import com.company.hrms.document.domain.model.DocumentId;
import com.company.hrms.document.domain.model.IDocumentRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DocumentRepositoryTest {

    @Autowired
    private IDocumentRepository repository;

    @Test
    @DisplayName("Should save and find document by ID")
    void shouldSaveAndFindById() {
        // Given
        DocumentId id = new DocumentId(UUID.randomUUID().toString());
        Document doc = Document.create(id, "repo-test.pdf", "user-repo");
        doc.addTag("important");

        // When
        repository.save(doc);
        Document found = repository.findById(id).orElseThrow();

        // Then
        assertNotNull(found);
        assertEquals(id, found.getId());
        assertEquals("repo-test.pdf", found.getFileName());
        assertTrue(found.getTags().contains("important"));
    }

    @Test
    @DisplayName("Should find documents using QueryEngine")
    void shouldFindDocumentsUsingQueryEngine() {
        // Given
        DocumentId id1 = new DocumentId(UUID.randomUUID().toString());
        Document doc1 = Document.create(id1, "report-2025.pdf", "user1");
        doc1.addTag("finance");
        repository.save(doc1);

        DocumentId id2 = new DocumentId(UUID.randomUUID().toString());
        Document doc2 = Document.create(id2, "manual.docx", "user2");
        doc2.addTag("training");
        repository.save(doc2);

        // When
        QueryGroup query = QueryBuilder.where()
                .eq("fileName", "report-2025.pdf")
                .build();

        Page<Document> result = repository.findDocuments(query, PageRequest.of(0, 10));

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals(id1, result.getContent().get(0).getId());
    }
}
