package com.company.hrms.organization.api.response.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 部門主管清單回應 DTO (含上層主管)
 */
@Data
@Builder
@Schema(description = "部門主管清單回應")
public class DepartmentManagersResponse {

    @Schema(description = "部門ID")
    private String departmentId;

    @Schema(description = "部門名稱")
    private String departmentName;

    @Schema(description = "主管清單 (由近到遠)")
    private List<ManagerInfo> managers;

    @Data
    @Builder
    @Schema(description = "主管資訊")
    public static class ManagerInfo {
        @Schema(description = "主管員工ID")
        private String employeeId;

        @Schema(description = "主管員工編號")
        private String employeeNumber;

        @Schema(description = "主管姓名")
        private String fullName;

        @Schema(description = "主管職稱")
        private String jobTitle;

        @Schema(description = "所屬部門ID")
        private String departmentId;

        @Schema(description = "所屬部門名稱")
        private String departmentName;

        @Schema(description = "管理層級 (1=直屬主管, 2=二級主管...)")
        private int managerLevel;
    }
}
