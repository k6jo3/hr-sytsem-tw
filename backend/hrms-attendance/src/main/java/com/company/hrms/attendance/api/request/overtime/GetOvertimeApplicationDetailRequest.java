package com.company.hrms.attendance.api.request.overtime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得加班申請詳情請求
 */
@Data
@Builder
@NoArgsConstructor
@Schema(description = "取得加班申請詳情請求")
public class GetOvertimeApplicationDetailRequest {
}
