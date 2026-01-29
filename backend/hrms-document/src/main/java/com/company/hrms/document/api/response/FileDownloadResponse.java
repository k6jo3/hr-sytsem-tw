package com.company.hrms.document.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 檔案下載回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadResponse {
    private String fileName;
    private String mimeType;
    private byte[] content; // 或是使用 InputStreamResource
}
