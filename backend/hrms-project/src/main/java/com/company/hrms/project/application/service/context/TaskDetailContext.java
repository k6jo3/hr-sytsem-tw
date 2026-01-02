package com.company.hrms.project.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.project.api.response.GetTaskDetailResponse;
import com.company.hrms.project.domain.model.aggregate.Task;

import lombok.Getter;
import lombok.Setter;

/**
 * 工項詳情查詢 Context
 */
@Getter
@Setter
public class TaskDetailContext extends PipelineContext {

    // 輸入
    private String taskId;

    // 中間結果
    private Task task;

    // 輸出
    private GetTaskDetailResponse response;

    public TaskDetailContext(String taskId) {
        this.taskId = taskId;
    }
}
