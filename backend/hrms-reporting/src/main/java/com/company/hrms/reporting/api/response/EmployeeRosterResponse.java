package com.company.hrms.reporting.api.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 員工花名冊回應
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "員工花名冊回應")
public class EmployeeRosterResponse {

    @Schema(description = "員工列表")
    private List<EmployeeRosterItem> content;

    @Schema(description = "總筆數")
    private Long totalElements;

    @Schema(description = "總頁數")
    private Integer totalPages;

    /**
     * 員工花名冊項目
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "員工花名冊項目")
    public static class EmployeeRosterItem {

        @Schema(description = "員工編號")
        private String employeeId;

        @Schema(description = "姓名")
        private String name;

        @Schema(description = "部門")
        private String departmentName;

        @Schema(description = "職位")
        private String positionName;

        @Schema(description = "到職日期")
        private LocalDate hireDate;

        @Schema(description = "年資(年)")
        private Double serviceYears;

        @Schema(description = "員工狀態")
        private String status;

        @Schema(description = "聯絡電話")
        private String phone;

        @Schema(description = "Email")
        private String email;
    }
}
