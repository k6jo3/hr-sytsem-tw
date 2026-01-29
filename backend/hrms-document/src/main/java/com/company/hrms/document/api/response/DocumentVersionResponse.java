package com.company.hrms.document.api.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件版本歷史回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersionResponse {
    private String documentId;
    private int currentVersion;
    private List<VersionInfo> versions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VersionInfo {
        private int version;
        private String fileName;
        private long fileSize;
        private String uploadedBy;
        private LocalDateTime uploadedAt;
        private String changeNote;
        private boolean isCurrent;
    }
}
