package com.company.hrms.project.api.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 移除專案成員回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveProjectMemberResponse {

    /**
     * 成員 ID
     */
    private String memberId;

    /**
     * 是否已移除
     */
    private boolean removed;

    /**
     * 離開日期
     */
    private LocalDate leaveDate;
}
