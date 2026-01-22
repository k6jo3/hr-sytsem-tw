package com.company.hrms.project.api.request;

import lombok.Data;

/**
 * 移除專案成員請求
 */
@Data
public class RemoveProjectMemberRequest {

    /**
     * 專案 ID
     */
    private String projectId;

    /**
     * 成員 ID
     */
    private String memberId;
}
