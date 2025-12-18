package com.company.hrms.organization.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationStatus;
import com.company.hrms.organization.domain.model.valueobject.OrganizationType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 組織聚合根
 * 代表一個法人公司（母公司或子公司）
 */
@Getter
@Builder
@EqualsAndHashCode(of = "id")
public class Organization {

    private final OrganizationId id;
    private String code;
    private String name;
    private String nameEn;
    private OrganizationType type;
    private OrganizationStatus status;
    private OrganizationId parentId;
    private String taxId;
    private String phone;
    private String fax;
    private String email;
    private String address;
    private LocalDate establishedDate;
    private String description;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立組織
     */
    public static Organization create(String code, String name, String nameEn, String taxId) {
        validateCode(code);
        validateName(name);

        return Organization.builder()
                .id(OrganizationId.generate())
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .type(OrganizationType.PARENT)
                .status(OrganizationStatus.ACTIVE)
                .taxId(taxId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 從持久層還原
     */
    public static Organization reconstitute(
            OrganizationId id,
            String code,
            String name,
            String nameEn,
            OrganizationType type,
            OrganizationStatus status,
            OrganizationId parentId,
            String taxId,
            String phone,
            String fax,
            String email,
            String address,
            LocalDate establishedDate,
            String description) {

        return Organization.builder()
                .id(id)
                .code(code)
                .name(name)
                .nameEn(nameEn)
                .type(type)
                .status(status)
                .parentId(parentId)
                .taxId(taxId)
                .phone(phone)
                .fax(fax)
                .email(email)
                .address(address)
                .establishedDate(establishedDate)
                .description(description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 更新組織資訊
     */
    public void update(String name, String nameEn, String taxId, String address, String phone) {
        if (this.status == OrganizationStatus.INACTIVE) {
            throw new DomainException("ORG_DEACTIVATED", "已停用組織無法更新");
        }
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        this.nameEn = nameEn;
        this.taxId = taxId;
        this.address = address;
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用組織
     */
    public void deactivate() {
        if (this.status == OrganizationStatus.INACTIVE) {
            throw new DomainException("ALREADY_DEACTIVATED", "組織已停用");
        }
        this.status = OrganizationStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 啟用組織
     */
    public void activate() {
        if (this.status == OrganizationStatus.ACTIVE) {
            throw new DomainException("ALREADY_ACTIVE", "組織已啟用");
        }
        this.status = OrganizationStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == OrganizationStatus.ACTIVE;
    }

    public boolean isParent() {
        return this.type == OrganizationType.PARENT;
    }

    // ==================== 驗證方法 ====================

    private static void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new DomainException("ORG_CODE_REQUIRED", "組織代碼不可為空");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("ORG_NAME_REQUIRED", "組織名稱不可為空");
        }
    }
}
