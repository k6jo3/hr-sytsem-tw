package com.company.hrms.common.infrastructure.web;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.company.hrms.common.api.response.ApiResponse;
import com.company.hrms.common.api.response.ErrorCode;
import com.company.hrms.common.application.pipeline.PipelineExecutionException;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.exception.ValidationException;

/**
 * 全域異常處理器
 * 統一處理 Controller 層拋出的異常，轉換為標準 API 回應格式
 *
 * <p>
 * 異常處理優先順序：
 * <ol>
 * <li>業務驗證異常 (ValidationException)</li>
 * <li>實體不存在異常 (EntityNotFoundException)</li>
 * <li>領域異常 (DomainException)</li>
 * <li>安全相關異常 (AuthenticationException, AccessDeniedException)</li>
 * <li>請求參數異常</li>
 * <li>其他未預期異常</li>
 * </ol>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        // ========================================
        // 領域異常處理
        // ========================================

        /**
         * 處理實體不存在異常
         */
        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
                log.warn("Entity not found: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.notFound(ex.getMessage()));
        }

        /**
         * 處理驗證異常
         */
        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex) {
                log.warn("Validation failed: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, ex.getMessage()));
        }

        /**
         * 處理領域異常
         */
        @ExceptionHandler(DomainException.class)
        public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException ex) {
                log.warn("Domain exception: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
        }

        /**
         * 處理 Pipeline 執行異常
         */
        @ExceptionHandler(PipelineExecutionException.class)
        public ResponseEntity<ApiResponse<Void>> handlePipelineExecutionException(PipelineExecutionException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof DomainException domainEx) {
                        log.warn("Pipeline task [{}] failed due to domain rule: {}", ex.getTaskName(),
                                        domainEx.getMessage());
                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(domainEx.getErrorCode(), domainEx.getMessage()));
                }
                if (cause instanceof ValidationException validationEx) {
                        log.warn("Pipeline task [{}] failed due to validation: {}", ex.getTaskName(),
                                        validationEx.getMessage());
                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, validationEx.getMessage()));
                }
                if (cause instanceof IllegalArgumentException illegalArgumentEx) {
                        log.warn("Pipeline task [{}] failed due to illegal argument: {}", ex.getTaskName(),
                                        illegalArgumentEx.getMessage());
                        return ResponseEntity
                                        .status(HttpStatus.BAD_REQUEST)
                                        .body(ApiResponse.badRequest(illegalArgumentEx.getMessage()));
                }
                if (cause instanceof EntityNotFoundException entityNotFoundEx) {
                        log.warn("Pipeline task [{}] failed due to entity not found: {}", ex.getTaskName(),
                                        entityNotFoundEx.getMessage());
                        return ResponseEntity
                                        .status(HttpStatus.NOT_FOUND)
                                        .body(ApiResponse.notFound(entityNotFoundEx.getMessage()));
                }

                log.error("Pipeline execution failed at task [{}]: {}", ex.getTaskName(), ex.getMessage(), ex);
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.serverError("處理流程執行失敗：" + ex.getMessage()));
        }

        // ========================================
        // 安全相關異常處理
        // ========================================

        /**
         * 處理認證異常
         */
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
                log.warn("Authentication failed: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.unauthorized("認證失敗：" + ex.getMessage()));
        }

        /**
         * 處理授權異常
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
                log.warn("Access denied: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.forbidden("存取被拒絕：權限不足"));
        }

        // ========================================
        // 請求參數異常處理
        // ========================================

        /**
         * 處理 @Valid 驗證失敗
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String fieldName = error instanceof FieldError
                                        ? ((FieldError) error).getField()
                                        : error.getObjectName();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                log.warn("Validation errors: {}", errors);
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR, "請求參數驗證失敗", errors));
        }

        /**
         * 處理缺少請求參數
         */
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
                        MissingServletRequestParameterException ex) {

                log.warn("Missing request parameter: {}", ex.getParameterName());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.badRequest("缺少必要參數：" + ex.getParameterName()));
        }

        /**
         * 處理參數類型不匹配
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex) {

                log.warn("Argument type mismatch: {} = {}", ex.getName(), ex.getValue());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.badRequest("參數類型錯誤：" + ex.getName()));
        }

        /**
         * 處理請求體無法解析
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
                        HttpMessageNotReadableException ex) {

                log.warn("Message not readable: {}", ex.getMessage());
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.badRequest("請求內容格式錯誤"));
        }

        // ========================================
        // HTTP 相關異常處理
        // ========================================

        /**
         * 處理路由不存在
         */
        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
                log.warn("No handler found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.notFound("API 路徑不存在"));
        }

        /**
         * 處理不支援的 HTTP 方法
         */
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiResponse<Void>> handleHttpRequestMethodNotSupportedException(
                        HttpRequestMethodNotSupportedException ex) {

                log.warn("Method not supported: {}", ex.getMethod());
                return ResponseEntity
                                .status(HttpStatus.METHOD_NOT_ALLOWED)
                                .body(ApiResponse.error(ErrorCode.METHOD_NOT_ALLOWED,
                                                "不支援的請求方法：" + ex.getMethod()));
        }

        /**
         * 處理不支援的媒體類型
         */
        @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
        public ResponseEntity<ApiResponse<Void>> handleHttpMediaTypeNotSupportedException(
                        HttpMediaTypeNotSupportedException ex) {

                log.warn("Media type not supported: {}", ex.getContentType());
                return ResponseEntity
                                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .body(ApiResponse.error(ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                                                "不支援的內容類型：" + ex.getContentType()));
        }

        // ========================================
        // 未預期異常處理
        // ========================================

        /**
         * 處理所有未預期的異常
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
                log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.serverError("系統發生未預期錯誤，請稍後再試"));
        }
}
