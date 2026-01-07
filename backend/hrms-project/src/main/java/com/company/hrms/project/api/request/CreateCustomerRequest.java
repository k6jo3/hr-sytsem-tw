package com.company.hrms.project.api.request;

import com.company.hrms.project.domain.model.command.CreateCustomerCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "建立客戶請求")
public class CreateCustomerRequest {

    @Schema(description = "客戶代碼", example = "CUST-001")
    private String customerCode;

    @Schema(description = "客戶名稱", example = "台積電")
    private String customerName;

    @Schema(description = "統一編號", example = "23301234")
    private String taxId;

    @Schema(description = "產業別", example = "半導體")
    private String industry;

    @Schema(description = "聯絡Email")
    private String email;

    @Schema(description = "聯絡電話")
    private String phoneNumber;

    public CreateCustomerCommand toCommand() {
        return CreateCustomerCommand.builder()
                .customerCode(customerCode)
                .customerName(customerName)
                .taxId(taxId)
                .industry(industry)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
    }
}
