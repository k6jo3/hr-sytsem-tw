package com.company.hrms.project.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskProgressResponse {
    private boolean success;
    private int currentProgress;
    private String status;
}
