package com.company.hrms.common.exception;

/**
 * 資源已存在異常
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    private final String errorCode;

    public ResourceAlreadyExistsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
