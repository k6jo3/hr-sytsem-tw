package com.company.hrms.iam.infrastructure.po;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系統參數持久化物件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "system_parameters")
public class SystemParameterPO {

    @Id
    private String id;

    private String paramCode;
    private String paramName;
    private String paramValue;
    private String paramType;
    private String module;
    private String category;
    private String description;
    private String defaultValue;
    private String tenantId;
    private Boolean isEncrypted;
    private Timestamp updatedAt;
    private String updatedBy;
    private Timestamp createdAt;
}
