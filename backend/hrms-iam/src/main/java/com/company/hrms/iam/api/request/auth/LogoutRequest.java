package com.company.hrms.iam.api.request.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登出請求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {

    /**
     * 要登出的 Token (Bearer Token)
     */
    private String token;
}
