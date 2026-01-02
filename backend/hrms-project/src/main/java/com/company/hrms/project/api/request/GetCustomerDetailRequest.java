package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢客戶詳情請求")
public class GetCustomerDetailRequest {

    @Schema(description = "客戶 ID")
    private String customerId;
}
