package com.company.hrms.organization.api.request.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 更新組織請求 DTO
 */
@Data
@Schema(description = "更新組織請求")
public class UpdateOrganizationRequest {

    @Size(max = 100, message = "組織名稱長度不可超過100字元")
    @Schema(description = "組織名稱", example = "台北總公司")
    private String name;

    @Size(max = 100, message = "英文名稱長度不可超過100字元")
    @Schema(description = "組織英文名稱", example = "Taipei Headquarters")
    private String nameEn;

    @Size(max = 20, message = "統一編號長度不可超過20字元")
    @Schema(description = "統一編號", example = "12345678")
    private String taxId;

    @Size(max = 20, message = "電話長度不可超過20字元")
    @Schema(description = "聯絡電話", example = "02-12345678")
    private String phone;

    @Size(max = 20, message = "傳真長度不可超過20字元")
    @Schema(description = "傳真", example = "02-12345679")
    private String fax;

    @Size(max = 100, message = "Email長度不可超過100字元")
    @Schema(description = "Email", example = "contact@company.com")
    private String email;

    @Size(max = 500, message = "地址長度不可超過500字元")
    @Schema(description = "地址", example = "台北市信義區信義路五段7號")
    private String address;

    @Schema(description = "成立日期", example = "2020-01-01")
    private LocalDate establishedDate;

    @Size(max = 1000, message = "說明長度不可超過1000字元")
    @Schema(description = "組織說明")
    private String description;
}
