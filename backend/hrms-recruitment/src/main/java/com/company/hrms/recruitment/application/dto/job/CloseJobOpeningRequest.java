package com.company.hrms.recruitment.application.dto.job;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "關閉職缺請求")
public class CloseJobOpeningRequest implements Serializable {

    @Schema(description = "關閉原因", requiredMode = Schema.RequiredMode.REQUIRED)
    private String reason;
}
