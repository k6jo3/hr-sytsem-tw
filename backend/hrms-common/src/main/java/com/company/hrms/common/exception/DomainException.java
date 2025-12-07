package com.company.hrms.common.exception;

/**
 * 領域例外 - 業務邏輯違規
 * 當業務規則被違反時拋出此例外
 * 
 * <p>
 * HTTP 狀態碼：400 Bad Request
 * </p>
 */
public class DomainException extends RuntimeException {

    private final String errorCode;

    public DomainException(String message) {
        super(message);
        this.errorCode = "DOMAIN_ERROR";
    }

    public DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DOMAIN_ERROR";
    }

    public String getErrorCode() {
        return errorCode;
    }
}
