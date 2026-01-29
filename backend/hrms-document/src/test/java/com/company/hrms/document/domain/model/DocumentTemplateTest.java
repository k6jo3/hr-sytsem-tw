package com.company.hrms.document.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.company.hrms.document.domain.model.enums.DocumentTemplateStatus;

class DocumentTemplateTest {

    @Test
    @DisplayName("Should create template with valid fields")
    void shouldCreateTemplate() {
        // Given
        DocumentTemplateId id = new DocumentTemplateId(UUID.randomUUID().toString());
        String name = "Employment Contract";
        String category = "CONTRACT";

        // When
        DocumentTemplate template = DocumentTemplate.create(id, "T001", name, category);

        // Then
        assertNotNull(template);
        assertEquals(id, template.getId());
        assertEquals(name, template.getName());
        assertEquals(category, template.getCategory());
        assertEquals(DocumentTemplateStatus.ACTIVE, template.getStatus());
    }

    @Test
    @DisplayName("Should change template status")
    void shouldChangeStatus() {
        // Given
        DocumentTemplate template = DocumentTemplate.create(
                new DocumentTemplateId(UUID.randomUUID().toString()), "CODE_TEST", "Test", "HR");

        // When
        template.deactivate();

        // Then
        assertEquals(DocumentTemplateStatus.INACTIVE, template.getStatus());

        // When
        template.activate();

        // Then
        assertEquals(DocumentTemplateStatus.ACTIVE, template.getStatus());
    }
}
