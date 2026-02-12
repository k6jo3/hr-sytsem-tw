package com.company.hrms.iam.api.request.user;

import com.company.hrms.common.api.request.PageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 查詢使用者列表請求
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查詢使用者列表請求")
public class GetUserListRequest extends PageRequest {

    /**
     * 搜尋關鍵字 (使用者名稱/Email/顯示名稱)
     */
    @Schema(description = "搜尋關鍵字 (使用者名稱/Email/顯示名稱)")
    private String keyword;

    /**
     * 狀態篩選
     */
    @Schema(description = "狀態篩選 (ACTIVE, INACTIVE, LOCKED, PENDING)")
    private String status;

    /**
     * 角色篩選
     */
    @Schema(description = "角色篩選 (角色ID)")
    private String roleId;

    /**
     * 員工 ID 篩選
     */
    @Schema(description = "員工 ID 篩選")
    private String employeeId;

    /**
     * 租戶 ID 篩選
     */
    @Schema(description = "租戶 ID 篩選")
    private String tenantId;
}
