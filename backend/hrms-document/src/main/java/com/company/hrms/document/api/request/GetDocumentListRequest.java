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
    private String folderId;
    private String parentId;
    private String name;
    private String documentType;
    private String ownerId;
    private String visibility;
    private String tag;
    private String classification;
    private List<String> accessibleVisibilities;
    private LocalDate startDate;
    private LocalDate endDate;
}
