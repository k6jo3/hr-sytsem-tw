package com.company.hrms.iam.api.response.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批次停用使用者回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDeactivateUsersResponse {

    /**
     * 成功停用的使用者 ID 列表
     */
    private List<String> successIds;

    /**
     * 失敗的使用者 ID 列表
     */
    private List<FailedUser> failedUsers;

    /**
     * 成功數量
     */
    private int successCount;

    /**
     * 失敗數量
     */
    private int failedCount;

    /**
     * 失敗的使用者資訊
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedUser {
        private String userId;
        private String reason;
    }
}
