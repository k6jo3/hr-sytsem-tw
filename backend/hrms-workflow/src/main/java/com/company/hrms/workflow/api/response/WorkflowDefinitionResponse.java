package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowDefinitionResponse {
    private String definitionId;
    private String flowName;
    private String flowType;
    private Boolean isActive;
    private Integer version;
    private LocalDateTime createdAt;
    // Nodes and Edges are typically loaded in Detail view, not list, but good to
    // have if needed.
    // For list view we might omit them to save bandwidth, but let's include for
    // simplicity or standard.
    // Actually, usually detail query vs list query. Let's make this general
    // response.
    private String nodes;
    private String edges;
}
