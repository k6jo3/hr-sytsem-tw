package com.company.hrms.organization.api.response.ess;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 個人資料回應 DTO (員工自助)
 */
@Data
@Builder
@Schema(description = "個人資料回應")
public class MyProfileResponse {

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "員工編號")
    private String employeeNumber;

    @Schema(description = "姓")
    private String firstName;

    @Schema(description = "名")
    private String lastName;

    @Schema(description = "全名")
    private String fullName;

    @Schema(description = "英文名")
    private String englishName;

    @Schema(description = "性別")
    private String gender;

    @Schema(description = "生日")
    private LocalDate birthDate;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "電話")
    private String phone;

    @Schema(description = "婚姻狀態")
    private String maritalStatus;

    @Schema(description = "地址")
    private AddressResponse address;

    @Schema(description = "緊急聯絡人")
    private EmergencyContactResponse emergencyContact;

    @Schema(description = "部門ID")
    private String departmentId;

    @Schema(description = "部門名稱")
    private String departmentName;

    @Schema(description = "職稱")
    private String jobTitle;

    @Schema(description = "職級")
    private String jobLevel;

    @Schema(description = "任職類型")
    private String employmentType;

    @Schema(description = "任職狀態")
    private String employmentStatus;

    @Schema(description = "到職日")
    private LocalDate hireDate;

    @Schema(description = "年資")
    private String seniority;

    @Schema(description = "直屬主管ID")
    private String supervisorId;

    @Schema(description = "直屬主管姓名")
    private String supervisorName;

    @Data
    @Builder
    @Schema(description = "地址回應")
    public static class AddressResponse {
        private String postalCode;
        private String city;
        private String district;
        private String street;
        private String fullAddress;
    }

    @Data
    @Builder
    @Schema(description = "緊急聯絡人回應")
    public static class EmergencyContactResponse {
        private String name;
        private String relationship;
        private String phone;
    }
}
