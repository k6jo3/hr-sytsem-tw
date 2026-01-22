package com.company.hrms.training.api.response;

import lombok.Data;

/**
 * 匯出報表回應
 */
@Data
public class ExportResponse {

    /**
     * 檔案名稱
     */
    private String fileName;

    /**
     * 內容類型 (MIME type)
     */
    private String contentType;

    /**
     * 檔案內容 (byte array)
     */
    private byte[] data;

    /**
     * 記錄數量
     */
    private int recordCount;
}
