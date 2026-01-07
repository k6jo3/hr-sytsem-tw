package com.company.hrms.project.application.service.context;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.project.api.response.GetMyProjectsResponse;
import com.company.hrms.project.api.response.GetMyProjectsResponse.MyProjectItemDto;
import com.company.hrms.project.domain.model.aggregate.Project;

import lombok.Getter;
import lombok.Setter;

/**
 * 我的專案查詢 Context
 * 
 * 用於在 Pipeline 中傳遞數據
 */
@Getter
@Setter
public class MyProjectsContext extends PipelineContext {

    // ===== 輸入 =====
    /**
     * 當前使用者 ID (員工 ID)
     */
    private UUID employeeId;

    /**
     * 分頁頁碼
     */
    private int page;

    /**
     * 分頁大小
     */
    private int size;

    // ===== 中間結果 =====
    /**
     * 查詢到的專案分頁結果
     */
    private Page<Project> projects;

    /**
     * 轉換後的 DTO 列表
     */
    private List<MyProjectItemDto> projectItems;

    // ===== 輸出 =====
    /**
     * 最終回應
     */
    private GetMyProjectsResponse response;

    /**
     * 建構子
     */
    public MyProjectsContext(UUID employeeId, int page, int size) {
        this.employeeId = employeeId;
        this.page = page;
        this.size = size;
    }
}
