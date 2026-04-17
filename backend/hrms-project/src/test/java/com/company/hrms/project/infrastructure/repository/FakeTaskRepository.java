package com.company.hrms.project.infrastructure.repository;

import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.project.domain.model.aggregate.Task;
import com.company.hrms.project.domain.model.valueobject.TaskId;
import com.company.hrms.project.domain.repository.ITaskRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ITaskRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeTaskRepository repo = new FakeTaskRepository();
 * repo.seed(task);
 *
 * List&lt;Task&gt; tasks = repo.findByProjectId(projectId);
 * assertThat(tasks).isNotEmpty();
 * </pre>
 */
public class FakeTaskRepository
        extends InMemoryBaseRepository<Task, TaskId>
        implements ITaskRepository {

    public FakeTaskRepository() {
        super(Task.class, Task::getId);
    }

    /**
     * 儲存任務（介面回傳實體）
     */
    @Override
    public Task save(Task task) {
        return doSave(task);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<Task> findById(TaskId id) {
        return super.findById(id);
    }

    /**
     * 依專案 ID 查詢所有任務
     */
    @Override
    public List<Task> findByProjectId(UUID projectId) {
        return findByField("projectId", projectId);
    }

    /**
     * 依父任務 ID 查詢子任務
     */
    @Override
    public List<Task> findByParentId(UUID parentId) {
        return findByField("parentTaskId", parentId);
    }

    /**
     * 檢查任務是否存在
     */
    @Override
    public boolean existsById(TaskId id) {
        return super.existsById(id);
    }
}
