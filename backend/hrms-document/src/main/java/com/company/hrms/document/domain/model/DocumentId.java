package com.company.hrms.document.domain.model;

import com.company.hrms.common.domain.model.Identifier;

public class DocumentId extends Identifier<String> {

    public DocumentId(String value) {
        super(value);
    }

    public static DocumentId next() {
        return new DocumentId(generateUUID());
    }
}
