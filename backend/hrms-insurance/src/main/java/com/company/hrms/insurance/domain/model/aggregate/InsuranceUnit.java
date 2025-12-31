package com.company.hrms.insurance.domain.model.aggregate;

import com.company.hrms.insurance.domain.model.valueobject.UnitId;

/**
 * 投保單位聚合根
 */
public class InsuranceUnit {
    private final UnitId id;
    private final String organizationId;
    private String unitCode;
    private String unitName;
    private String laborInsuranceNumber;
    private String healthInsuranceNumber;
    private String pensionNumber;
    private boolean isActive;

    public InsuranceUnit(
            UnitId id,
            String organizationId,
            String unitCode,
            String unitName) {

        if (id == null)
            throw new IllegalArgumentException("UnitId cannot be null");
        if (organizationId == null || organizationId.isBlank())
            throw new IllegalArgumentException("OrganizationId cannot be null or blank");
        if (unitCode == null || unitCode.isBlank())
            throw new IllegalArgumentException("UnitCode cannot be null or blank");
        if (unitName == null || unitName.isBlank())
            throw new IllegalArgumentException("UnitName cannot be null or blank");

        this.id = id;
        this.organizationId = organizationId;
        this.unitCode = unitCode;
        this.unitName = unitName;
        this.isActive = true;
    }

    /**
     * 靜態工廠方法 - 建立投保單位
     */
    public static InsuranceUnit create(String organizationId, String unitCode, String unitName) {
        return new InsuranceUnit(UnitId.generate(), organizationId, unitCode, unitName);
    }

    /**
     * 設定勞保證號
     */
    public void setLaborInsuranceNumber(String laborInsuranceNumber) {
        this.laborInsuranceNumber = laborInsuranceNumber;
    }

    /**
     * 設定健保證號
     */
    public void setHealthInsuranceNumber(String healthInsuranceNumber) {
        this.healthInsuranceNumber = healthInsuranceNumber;
    }

    /**
     * 設定勞退提繳單位編號
     */
    public void setPensionNumber(String pensionNumber) {
        this.pensionNumber = pensionNumber;
    }

    /**
     * 停用投保單位
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 啟用投保單位
     */
    public void activate() {
        this.isActive = true;
    }

    // Getters
    public UnitId getId() {
        return id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getLaborInsuranceNumber() {
        return laborInsuranceNumber;
    }

    public String getHealthInsuranceNumber() {
        return healthInsuranceNumber;
    }

    public String getPensionNumber() {
        return pensionNumber;
    }

    public boolean isActive() {
        return isActive;
    }
}
