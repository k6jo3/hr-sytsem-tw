package com.company.hrms.training.api.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.training.domain.model.valueobject.CertificateStatus;
import com.company.hrms.training.domain.model.valueobject.CourseCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "證照詳細資訊回應")
public class CertificateResponse {

    @Schema(description = "證照ID")
    private String certificateId;

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "證照名稱")
    private String certificateName;

    @Schema(description = "發證機構")
    private String issuingOrganization;

    @Schema(description = "證照編號")
    private String certificateNumber;

    @Schema(description = "發證日期")
    private LocalDate issueDate;

    @Schema(description = "到期日期")
    private LocalDate expiryDate;

    @Schema(description = "類別")
    private CourseCategory category;

    @Schema(description = "是否為必要證照")
    private Boolean isRequired;

    @Schema(description = "附件URL")
    private String attachmentUrl;

    @Schema(description = "備註")
    private String remarks;

    @Schema(description = "是否已驗證")
    private Boolean isVerified;

    @Schema(description = "驗證人")
    private String verifiedBy;

    @Schema(description = "驗證時間")
    private LocalDateTime verifiedAt;

    @Schema(description = "狀態")
    private CertificateStatus status;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
