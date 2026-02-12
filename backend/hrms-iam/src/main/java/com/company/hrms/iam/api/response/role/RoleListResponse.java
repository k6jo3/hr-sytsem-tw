package com.company.hrms.iam.api.response.role;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色列表項目回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleListResponse {

    /**
     * 角色 ID
     */
    private String roleId;

    /**
     * 角色名稱
     */
    private String roleName;

    /**
     * 角色代碼
     */
    private String roleCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否為系統角色
     */
    @JsonProperty("isSystemRole")
    private boolean isSystemRole;

    /**
     * 狀態
     */
    private String status;

    /**
     * 權限數量
     */
    private int permissionCount;

    /**
     * 權限清單 (簡要)
     */
    private List<String> permissions;

    /**
     * 建立時間
     */
    private LocalDateTime createdAt;
}
