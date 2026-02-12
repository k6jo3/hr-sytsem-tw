package com.company.hrms.iam.api.request.role;

import com.company.hrms.common.api.request.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 查詢角色列表請求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查詢角色列表請求")
public class GetRoleListRequest extends PageRequest {

    /**
     * 角色名稱/代碼搜尋
     */
    @Schema(description = "角色名稱/代碼搜尋")
    private String name;

    /**
     * 狀態篩選
     */
    @Schema(description = "狀態篩選 (ACTIVE, INACTIVE)")
    private String status;

    /**
     * 是否為系統角色篩選
     */
    @Schema(description = "是否為系統角色篩選")
    private Boolean isSystemRole;

    /**
     * 角色類型
     */
    @Schema(description = "角色類型")
    private String type;

    /**
     * 租戶 ID
     */
    @Schema(description = "租戶 ID")
    private String tenantId;
}
