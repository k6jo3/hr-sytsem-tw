package com.company.hrms.iam.api.request.user;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批次停用使用者請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDeactivateUsersRequest {

    /**
     * 使用者 ID 列表
     */
    @NotEmpty(message = "使用者 ID 列表不可為空")
    private List<String> userIds;
}
