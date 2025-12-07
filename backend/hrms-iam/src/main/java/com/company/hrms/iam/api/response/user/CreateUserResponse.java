package com.company.hrms.iam.api.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增使用者回應 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserResponse {

    /**
     * 使用者 ID
     */
    private String userId;

    /**
     * 使用者名稱
     */
    private String username;

    /**
     * 訊息
     */
    private String message;

    public CreateUserResponse(String userId) {
        this.userId = userId;
        this.message = "使用者建立成功";
    }
}
