package com.company.hrms.common.exception;

/**
 * 實體未找到例外
 * 當查詢的資源不存在時拋出此例外
 * 
 * <p>
 * HTTP 狀態碼：404 Not Found
 * </p>
 */
public class EntityNotFoundException extends RuntimeException {

    private final String entityType;
    private final String entityId;

    public EntityNotFoundException(String entityType, String entityId) {
        super(String.format("%s with id '%s' not found", entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public EntityNotFoundException(String message) {
        super(message);
        this.entityType = "Entity";
        this.entityId = "unknown";
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }
}
