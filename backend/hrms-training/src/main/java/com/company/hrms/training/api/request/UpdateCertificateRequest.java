package com.company.hrms.training.api.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新證照請求")
public class UpdateCertificateRequest {

    @Schema(description = "證照編號")
    private String certificateNumber;

    @Schema(description = "發證日期")
    private LocalDate issueDate;

    @Schema(description = "到期日期")
    private LocalDate expiryDate;

    @Schema(description = "附件URL")
    private String attachmentUrl;

    @Schema(description = "備註")
    private String remarks;
}
