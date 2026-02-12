package com.company.hrms.iam.api.response.user;

/**
 * 新增使用者回應 VO
 */
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

    public CreateUserResponse() {
    }

    public CreateUserResponse(String userId, String username, String message) {
        this.userId = userId;
        this.username = username;
        this.message = message;
    }

    public CreateUserResponse(String userId) {
        this.userId = userId;
        this.message = "使用者建立成功";
    }

    public static CreateUserResponseBuilder builder() {
        return new CreateUserResponseBuilder();
    }

    public static class CreateUserResponseBuilder {
        private String userId;
        private String username;
        private String message;

        public CreateUserResponseBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public CreateUserResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public CreateUserResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public CreateUserResponse build() {
            return new CreateUserResponse(userId, username, message);
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
