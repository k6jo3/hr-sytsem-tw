package com.company.hrms.organization.api.response.department;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 新增部門回應 DTO
 */
@Data
@Builder
@Schema(description = "新增部門回應")
public class CreateDepartmentResponse {

    @Schema(description = "部門ID")
    private String departmentId;

    @Schema(description = "部門代碼")
    private String code;

    @Schema(description = "部門名稱")
    private String name;

    @Schema(description = "部門層級")
    private int level;

    @Schema(description = "訊息")
    private String message;

    public static CreateDepartmentResponse success(String departmentId, String code, String name, int level) {
        return CreateDepartmentResponse.builder()
                .departmentId(departmentId)
                .code(code)
                .name(name)
                .level(level)
                .message("部門新增成功")
                .build();
    }
}
