package com.company.hrms.common.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * 驗證例外 - 參數驗證失敗
 * 當請求參數驗證失敗時拋出此例外
 * 
 * <p>
 * HTTP 狀態碼：400 Bad Request
 * </p>
 */
public class ValidationException extends RuntimeException {

    private final List<ValidationError> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    public ValidationException(List<ValidationError> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public ValidationException(String field, String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(new ValidationError(field, message));
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    /**
     * 驗證錯誤詳情
     */
    public static class ValidationError {
        private final String field;
        private final String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}
