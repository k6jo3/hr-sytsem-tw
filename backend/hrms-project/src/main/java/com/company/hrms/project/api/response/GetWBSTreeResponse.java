package com.company.hrms.project.api.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetWBSTreeResponse {
    private String projectId;
    private List<TaskTreeNodeDto> rootTasks;
}
