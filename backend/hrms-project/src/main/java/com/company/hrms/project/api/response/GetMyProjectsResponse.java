package com.company.hrms.project.api.response;

import java.math.BigDecimal;
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
public class GetMyProjectsResponse {
    private List<MyProjectItemDto> items;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyProjectItemDto {
        private String projectId;
        private String projectCode;
        private String projectName;
        private String status;
        private String role; // 專案中的角色
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal budget;
        private Integer progress;
    }
}
