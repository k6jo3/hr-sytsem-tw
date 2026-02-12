package com.company.hrms.common.api.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 通用 API 回應包裝類
 * 提供統一的回應格式，包含狀態碼、訊息、資料與時間戳記
 *
 * <p>
 * 使用範例：
 * 
 * <pre>
 * // 成功回應
 * return ApiResponse.success(userData);
 *
 * // 錯誤回應
 * return ApiResponse.error(ErrorCode.USER_NOT_FOUND, "使用者不存在");
 *
 * // 帶分頁的成功回應
 * return ApiResponse.success(PageResponse.of(items, page, size, total));
 * </pre>
 *
 * @param <T> 資料類型
 */
@Schema(description = "API 回應包裝")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Schema(description = "是否成功", example = "true")
    private boolean success;

    @Schema(description = "狀態碼", example = "200")
    private String code;

    @Schema(description = "訊息", example = "操作成功")
    private String message;

    @Schema(description = "回應資料")
    private T data;

    @Schema(description = "回應時間戳記")
    private LocalDateTime timestamp;

    @Schema(description = "追蹤 ID（用於問題追蹤）")
    private String traceId;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 建立成功回應（無資料）
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "200", "操作成功", null);
    }

    /**
     * 建立成功回應（帶資料）
     *
     * @param data 回應資料
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "200", "操作成功", data);
    }

    /**
     * 建立成功回應（帶訊息與資料）
     *
     * @param message 成功訊息
     * @param data    回應資料
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "200", message, data);
    }

    /**
     * 建立錯誤回應
     *
     * @param code    錯誤碼
     * @param message 錯誤訊息
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }

    /**
     * 建立錯誤回應（帶資料）
     *
     * @param code    錯誤碼
     * @param message 錯誤訊息
     * @param data    額外資料（如驗證錯誤詳情）
     */
    public static <T> ApiResponse<T> error(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }

    /**
     * 建立 400 Bad Request 錯誤回應
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(false, "400", message, null);
    }

    /**
     * 建立 401 Unauthorized 錯誤回應
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(false, "401", message, null);
    }

    /**
     * 建立 403 Forbidden 錯誤回應
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(false, "403", message, null);
    }

    /**
     * 建立 404 Not Found 錯誤回應
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, "404", message, null);
    }

    /**
     * 建立 500 Internal Server Error 錯誤回應
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return new ApiResponse<>(false, "500", message, null);
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    /**
     * 設定追蹤 ID 並返回自身（支援鏈式呼叫）
     */
    public ApiResponse<T> withTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
