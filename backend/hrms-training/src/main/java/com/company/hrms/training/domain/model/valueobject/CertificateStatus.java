package com.company.hrms.training.domain.model.valueobject;

public enum CertificateStatus {
    VALID("有效"),
    EXPIRED("已過期"),
    EXPIRING("即將到期"),
    REVOKED("已撤銷");

    private final String label;

    CertificateStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
