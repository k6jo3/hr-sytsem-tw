package com.company.hrms.document.domain.model;

import com.company.hrms.common.domain.model.AggregateRoot;
import com.company.hrms.document.domain.model.enums.DocumentTemplateStatus;

import lombok.Getter;

@Getter
public class DocumentTemplate extends AggregateRoot<DocumentTemplateId> {
    private String code;
    private String name;
    private String content;
    private String category;
    private DocumentTemplateStatus status;

    protected DocumentTemplate() {
        super(null);
    }

    private DocumentTemplate(DocumentTemplateId id) {
        super(id);
    }

    public static DocumentTemplate create(DocumentTemplateId id, String code, String name, String category) {
        DocumentTemplate t = new DocumentTemplate(id);
        t.code = code;
        t.name = name;
        t.category = category;
        t.status = DocumentTemplateStatus.ACTIVE;
        return t;
    }

    public static DocumentTemplate reconstitute(DocumentTemplateId id, String code, String name, String content,
            String category, DocumentTemplateStatus status) {
        DocumentTemplate t = new DocumentTemplate(id);
        t.code = code;
        t.name = name;
        t.content = content;
        t.category = category;
        t.status = status;
        return t;
    }

    public void setContent(String content) {
        this.content = content;
        this.touch();
    }

    public void activate() {
        this.status = DocumentTemplateStatus.ACTIVE;
        this.touch();
    }

    public void deactivate() {
        this.status = DocumentTemplateStatus.INACTIVE;
        this.touch();
    }
}
