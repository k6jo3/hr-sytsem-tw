package com.company.hrms.common.application.pipeline;

/**
 * Pipeline 執行異常
 * 當 Pipeline 中的任務執行失敗時拋出
 */
public class PipelineExecutionException extends RuntimeException {

    private final String taskName;

    /**
     * 建立 Pipeline 執行異常
     *
     * @param taskName 失敗的任務名稱
     * @param cause 原始異常
     */
    public PipelineExecutionException(String taskName, Throwable cause) {
        super("Pipeline task [" + taskName + "] failed: " + cause.getMessage(), cause);
        this.taskName = taskName;
    }

    /**
     * 取得失敗的任務名稱
     */
    public String getTaskName() {
        return taskName;
    }
}
