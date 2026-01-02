package com.company.hrms.project.api.request;

import com.company.hrms.project.domain.model.command.UpdateCustomerCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "更新客戶請求")
public class UpdateCustomerRequest {

    @Schema(description = "客戶ID", hidden = true)
    private String customerId;

    @Schema(description = "客戶名稱")
    private String customerName;

    @Schema(description = "統一編號")
    private String taxId;

    @Schema(description = "產業別")
    private String industry;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "電話")
    private String phoneNumber;

    public UpdateCustomerCommand toCommand() {
        return UpdateCustomerCommand.builder()
                .customerName(customerName)
                .taxId(taxId)
                .industry(industry)
                .email(email)
                .phoneNumber(phoneNumber)
                .build();
    }
}
