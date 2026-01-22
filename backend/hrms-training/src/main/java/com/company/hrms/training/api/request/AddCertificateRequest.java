package com.company.hrms.training.api.request;

import java.time.LocalDate;

import com.company.hrms.training.domain.model.valueobject.CourseCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "新增證照請求")
public class AddCertificateRequest {

    @Schema(description = "員工ID")
    @NotBlank(message = "員工ID不能為空")
    private String employeeId;

    @Schema(description = "證照名稱")
    @NotBlank(message = "證照名稱不能為空")
    private String certificateName;

    @Schema(description = "發證機構")
    private String issuingOrganization;

    @Schema(description = "證照編號")
    private String certificateNumber;

    @Schema(description = "發證日期")
    @NotNull(message = "發證日期不能為空")
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
}
