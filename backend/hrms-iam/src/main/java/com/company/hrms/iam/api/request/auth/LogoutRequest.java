package com.company.hrms.iam.api.request.auth;

/**
 * 登出請求 DTO
 */
public class LogoutRequest {

    public LogoutRequest() {
    }

    public LogoutRequest(String token) {
        this.token = token;
    }

    public static LogoutRequestBuilder builder() {
        return new LogoutRequestBuilder();
    }

    public static class LogoutRequestBuilder {
        private String token;

        public LogoutRequestBuilder token(String token) {
            this.token = token;
            return this;
        }

        public LogoutRequest build() {
            return new LogoutRequest(token);
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 要登出的 Token (Bearer Token)
     */
    private String token;
}
