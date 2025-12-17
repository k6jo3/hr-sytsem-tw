package com.company.hrms.organization.domain.model.aggregate;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationStatus;
import com.company.hrms.organization.domain.model.valueobject.OrganizationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 組織聚合根
 * 代表一個法人公司（母公司或子公司）
 */
@Getter
@Builder
public class Organization {

    /**
     * 組織 ID
     */
    private final OrganizationId id;

    /**
     * 組織代碼 (唯一)
     */
    private String organizationCode;

    /**
     * 組織名稱
     */
    private String organizationName;

    /**
     * 組織類型 (母公司/子公司)
     */
    private OrganizationType organizationType;

    /**
     * 母公司 ID (子公司才有)
     */
    private UUID parentOrganizationId;

    /**
     * 統一編號
     */
    private String taxId;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 電話號碼
     */
    private String phoneNumber;

    /**
     * 成立日期
     */
    private LocalDate establishedDate;

    /**
     * 組織狀態
     */
    private OrganizationStatus status;

    /**
     * 建立時間
     */
    private final LocalDateTime createdAt;

    /**
     * 更新時間
     */
    private LocalDateTime updatedAt;

    // ==================== 工廠方法 ====================

    /**
     * 建立母公司
     * @param organizationCode 組織代碼
     * @param organizationName 組織名稱
     * @param taxId 統一編號
     * @return 新的 Organization 實例
     */
    public static Organization createParent(String organizationCode, String organizationName, String taxId) {
        validateOrganizationCode(organizationCode);
        validateOrganizationName(organizationName);

        return Organization.builder()
                .id(OrganizationId.generate())
                .organizationCode(organizationCode)
                .organizationName(organizationName)
                .organizationType(OrganizationType.PARENT)
                .taxId(taxId)
                .status(OrganizationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 建立子公司
     * @param organizationCode 組織代碼
     * @param organizationName 組織名稱
     * @param parentOrganizationId 母公司 ID
     * @param taxId 統一編號
     * @return 新的 Organization 實例
     */
    public static Organization createSubsidiary(String organizationCode, String organizationName,
                                                 UUID parentOrganizationId, String taxId) {
        validateOrganizationCode(organizationCode);
        validateOrganizationName(organizationName);

        if (parentOrganizationId == null) {
            throw new DomainException("PARENT_ORG_REQUIRED", "子公司必須指定母公司");
        }

        return Organization.builder()
                .id(OrganizationId.generate())
                .organizationCode(organizationCode)
                .organizationName(organizationName)
                .organizationType(OrganizationType.SUBSIDIARY)
                .parentOrganizationId(parentOrganizationId)
                .taxId(taxId)
                .status(OrganizationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== 業務方法 ====================

    /**
     * 更新組織資訊
     * @param organizationName 組織名稱
     * @param address 地址
     * @param phoneNumber 電話號碼
     */
    public void updateInfo(String organizationName, String address, String phoneNumber) {
        if (organizationName != null && !organizationName.isBlank()) {
            this.organizationName = organizationName;
        }
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 停用組織
     * @throws DomainException 若有在職員工則無法停用
     */
    public void deactivate() {
        if (this.status == OrganizationStatus.INACTIVE) {
            throw new DomainException("ORG_ALREADY_INACTIVE", "組織已停用");
        }
        this.status = OrganizationStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 啟用組織
     */
    public void activate() {
        if (this.status == OrganizationStatus.ACTIVE) {
            throw new DomainException("ORG_ALREADY_ACTIVE", "組織已啟用");
        }
        this.status = OrganizationStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 是否為母公司
     * @return 是否為母公司
     */
    public boolean isParent() {
        return this.organizationType == OrganizationType.PARENT;
    }

    /**
     * 是否為子公司
     * @return 是否為子公司
     */
    public boolean isSubsidiary() {
        return this.organizationType == OrganizationType.SUBSIDIARY;
    }

    /**
     * 是否啟用中
     * @return 是否啟用中
     */
    public boolean isActive() {
        return this.status.isActive();
    }

    // ==================== 驗證方法 ====================

    private static void validateOrganizationCode(String code) {
        if (code == null || code.isBlank()) {
            throw new DomainException("ORG_CODE_REQUIRED", "組織代碼不可為空");
        }
        if (code.length() > 50) {
            throw new DomainException("ORG_CODE_TOO_LONG", "組織代碼長度不可超過50字元");
        }
    }

    private static void validateOrganizationName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("ORG_NAME_REQUIRED", "組織名稱不可為空");
        }
        if (name.length() > 255) {
            throw new DomainException("ORG_NAME_TOO_LONG", "組織名稱長度不可超過255字元");
        }
    }
}
