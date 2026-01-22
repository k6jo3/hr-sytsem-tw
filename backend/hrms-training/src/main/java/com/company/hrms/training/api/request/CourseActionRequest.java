package com.company.hrms.training.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "課程操作通用請求 (保留擴展)")
public class CourseActionRequest {
    // 暫時為空，用於 publish, complete 等不需要額外參數的操作
}
