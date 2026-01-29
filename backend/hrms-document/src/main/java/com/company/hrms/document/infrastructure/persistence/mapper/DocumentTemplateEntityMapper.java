package com.company.hrms.document.infrastructure.persistence.mapper;

import com.company.hrms.document.domain.model.DocumentTemplate;
import com.company.hrms.document.domain.model.DocumentTemplateId;
import com.company.hrms.document.domain.model.enums.DocumentTemplateStatus;
import com.company.hrms.document.infrastructure.persistence.po.DocumentTemplatePO;

/**
 * 文件範本實體轉換器
 */
public class DocumentTemplateEntityMapper {

    public static DocumentTemplate toDomain(DocumentTemplatePO po) {
        if (po == null) {
            return null;
        }

        return DocumentTemplate.reconstitute(
                new DocumentTemplateId(po.getId()),
                po.getCode(),
                po.getName(),
                po.getContent(),
                po.getCategory(),
                po.getStatus() != null ? DocumentTemplateStatus.valueOf(po.getStatus()) : null);
    }

    public static DocumentTemplatePO toPO(DocumentTemplate domain) {
        if (domain == null) {
            return null;
        }

        DocumentTemplatePO po = new DocumentTemplatePO();
        po.setId(domain.getId().getValue());
        po.setCode(domain.getCode());
        po.setName(domain.getName());
        po.setContent(domain.getContent());
        po.setCategory(domain.getCategory());

        if (domain.getStatus() != null) {
            po.setStatus(domain.getStatus().name());
        }

        return po;
    }
}
