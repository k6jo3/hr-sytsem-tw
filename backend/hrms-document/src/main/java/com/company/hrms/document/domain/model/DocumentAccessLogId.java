package com.company.hrms.document.domain.model;

import com.company.hrms.common.domain.model.Identifier;

import lombok.Getter;

@Getter
public class DocumentAccessLogId extends Identifier<String> {
    public DocumentAccessLogId(String value) {
        super(value);
    }
}
