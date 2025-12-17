package com.company.hrms.organization.api.request.ess;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新個人資料請求 DTO (員工自助)
 */
@Data
@Schema(description = "更新個人資料請求")
public class UpdateMyProfileRequest {

    @Size(max = 20, message = "電話長度不可超過20字元")
    @Schema(description = "聯絡電話", example = "0912345678")
    private String phone;

    @Schema(description = "婚姻狀態", allowableValues = {"SINGLE", "MARRIED", "DIVORCED", "WIDOWED"})
    private String maritalStatus;

    @Schema(description = "地址")
    private AddressDto address;

    @Schema(description = "緊急聯絡人")
    private EmergencyContactDto emergencyContact;

    @Schema(description = "銀行帳戶")
    private BankAccountDto bankAccount;

    @Data
    @Schema(description = "地址 DTO")
    public static class AddressDto {
        @Schema(description = "郵遞區號", example = "110")
        private String postalCode;

        @Schema(description = "縣市", example = "台北市")
        private String city;

        @Schema(description = "區", example = "信義區")
        private String district;

        @Schema(description = "街道地址", example = "信義路五段7號")
        private String street;
    }

    @Data
    @Schema(description = "緊急聯絡人 DTO")
    public static class EmergencyContactDto {
        @Schema(description = "姓名", example = "王小明")
        private String name;

        @Schema(description = "關係", example = "配偶")
        private String relationship;

        @Schema(description = "電話", example = "0912345678")
        private String phone;
    }

    @Data
    @Schema(description = "銀行帳戶 DTO")
    public static class BankAccountDto {
        @Schema(description = "銀行代碼", example = "004")
        private String bankCode;

        @Schema(description = "分行代碼", example = "0017")
        private String branchCode;

        @Schema(description = "帳號", example = "12345678901234")
        private String accountNumber;

        @Schema(description = "戶名", example = "王大明")
        private String accountHolderName;
    }
}
