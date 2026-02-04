package com.company.hrms.document.api.request;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(callSuper = true)
public class GetDocumentListRequest extends com.company.hrms.common.api.request.PageRequest {
    @com.company.hrms.common.query.QueryCondition.EQ("folderId")
    private String folderId;

    // Manual handling for complex logic (NULL_MARKER)
    private String parentId;

    @com.company.hrms.common.query.QueryCondition.LIKE("fileName")
    private String name;

    @com.company.hrms.common.query.QueryCondition.EQ("documentType")
    private String documentType;

    @com.company.hrms.common.query.QueryCondition.EQ("ownerId")
    private String ownerId;

    @com.company.hrms.common.query.QueryCondition.EQ("visibility")
    private String visibility;

    @com.company.hrms.common.query.QueryCondition.LIKE("tags")
    private String tag;

    @com.company.hrms.common.query.QueryCondition.EQ("classification")
    private String classification;

    @com.company.hrms.common.query.QueryCondition.IN("visibility")
    private List<String> accessibleVisibilities;
    @com.company.hrms.common.query.QueryCondition.GTE("updatedAt")
    private LocalDate startDate;
    private LocalDate endDate;
}
