package com.company.hrms.organization.api.response.ess;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 證明文件申請回應 DTO
 */
@Data
@Builder
@Schema(description = "證明文件申請回應")
public class CertificateRequestResponse {

    @Schema(description = "申請ID")
    private String requestId;

    @Schema(description = "證明類型")
    private String certificateType;

    @Schema(description = "證明類型顯示名稱")
    private String certificateTypeDisplay;

    @Schema(description = "份數")
    private int copies;

    @Schema(description = "用途")
    private String purpose;

    @Schema(description = "申請狀態")
    private String status;

    @Schema(description = "申請狀態顯示名稱")
    private String statusDisplay;

    @Schema(description = "申請日期")
    private LocalDate requestDate;

    @Schema(description = "完成日期")
    private LocalDate completedDate;

    @Schema(description = "備註")
    private String remarks;

    /**
     * 證明文件申請清單回應
     */
    @Data
    @Builder
    @Schema(description = "證明文件申請清單回應")
    public static class ListResponse {
        @Schema(description = "申請清單")
        private List<CertificateRequestResponse> items;

        @Schema(description = "總筆數")
        private int totalCount;
    }
}
