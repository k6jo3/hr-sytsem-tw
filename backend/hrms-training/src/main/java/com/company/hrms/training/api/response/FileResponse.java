package com.company.hrms.training.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "檔案下載回應")
public class FileResponse {

    @Schema(description = "檔案名稱")
    private String fileName;

    @Schema(description = "檔案類型")
    private String contentType;

    @Schema(description = "Base64編碼內容")
    private String content;
}
