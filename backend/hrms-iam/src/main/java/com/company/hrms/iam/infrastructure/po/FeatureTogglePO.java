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
 * 功能開關持久化物件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feature_toggles")
public class FeatureTogglePO {

    @Id
    private String id;

    private String featureCode;
    private String featureName;
    private String module;
    private Boolean enabled;
    private String description;
    private String tenantId;
    private Timestamp updatedAt;
    private String updatedBy;
    private Timestamp createdAt;
}
