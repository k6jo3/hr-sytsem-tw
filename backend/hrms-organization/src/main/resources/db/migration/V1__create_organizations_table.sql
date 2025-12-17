-- HR02 組織員工服務 - V1: 建立組織表
-- 版本: 1.0
-- 日期: 2025-12-17

CREATE TABLE organizations (
    organization_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_code VARCHAR(50) UNIQUE NOT NULL,
    organization_name VARCHAR(255) NOT NULL,
    organization_type VARCHAR(20) NOT NULL,
    parent_organization_id UUID REFERENCES organizations(organization_id),
    tax_id VARCHAR(20),
    address TEXT,
    phone_number VARCHAR(50),
    established_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_org_type CHECK (organization_type IN ('PARENT', 'SUBSIDIARY')),
    CONSTRAINT chk_org_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_organizations_parent_id ON organizations(parent_organization_id);
CREATE INDEX idx_organizations_status ON organizations(status);

COMMENT ON TABLE organizations IS '組織/公司表';
COMMENT ON COLUMN organizations.organization_code IS '公司代號 (唯一)';
COMMENT ON COLUMN organizations.organization_type IS '組織類型: PARENT(母公司)/SUBSIDIARY(子公司)';
COMMENT ON COLUMN organizations.tax_id IS '統一編號';
