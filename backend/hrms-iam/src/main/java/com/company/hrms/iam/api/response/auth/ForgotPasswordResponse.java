package com.company.hrms.iam.api.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 忘記密碼回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponse {

    /**
     * 訊息
     */
    private String message;

    /**
     * 成功建立回應
     */
    public static ForgotPasswordResponse success() {
        return ForgotPasswordResponse.builder()
                .message("密碼重置連結已發送至您的信箱，請於 30 分鐘內完成重置")
                .build();
    }
}
