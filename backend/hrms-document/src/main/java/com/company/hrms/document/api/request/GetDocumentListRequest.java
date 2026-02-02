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
public class GetDocumentListRequest {
    @com.company.hrms.common.query.QueryCondition.EQ("folder_id")
    private String folderId;

    // Manual handling for complex logic (NULL_MARKER)
    private String parentId;

    @com.company.hrms.common.query.QueryCondition.LIKE("name")
    private String name;

    @com.company.hrms.common.query.QueryCondition.EQ("type")
    private String documentType;

    @com.company.hrms.common.query.QueryCondition.EQ("owner_id")
    private String ownerId;

    @com.company.hrms.common.query.QueryCondition.EQ("visibility")
    private String visibility;

    @com.company.hrms.common.query.QueryCondition.LIKE("tags")
    private String tag;

    @com.company.hrms.common.query.QueryCondition.EQ("classification")
    private String classification;

    @com.company.hrms.common.query.QueryCondition.IN("visibility")
    private List<String> accessibleVisibilities;
    @com.company.hrms.common.query.QueryCondition.GTE("updated_at")
    private LocalDate startDate;
    private LocalDate endDate;
}
