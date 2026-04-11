package com.company.hrms.project.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * IProjectRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeProjectRepository repo = new FakeProjectRepository();
 * repo.seed(project);
 *
 * Optional&lt;Project&gt; found = repo.findById(projectId);
 * assertThat(found).isPresent();
 * </pre>
 */
public class FakeProjectRepository
        extends InMemoryBaseRepository<Project, ProjectId>
        implements IProjectRepository {

    public FakeProjectRepository() {
        super(Project.class, Project::getId);
    }

    /**
     * 儲存專案（介面回傳實體）
     */
    @Override
    public Project save(Project project) {
        return doSave(project);
    }

    /**
     * 分頁查詢專案
     */
    @Override
    public Page<Project> findProjects(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<Project> findById(ProjectId id) {
        return super.findById(id);
    }

    /**
     * 查詢所有專案
     */
    @Override
    public List<Project> findAll() {
        return super.findAll();
    }

    /**
     * 檢查專案是否存在
     */
    @Override
    public boolean existsById(ProjectId id) {
        return super.existsById(id);
    }

    /**
     * 根據 ID 刪除
     */
    @Override
    public void deleteById(ProjectId id) {
        super.deleteById(id);
    }

    /**
     * 根據成員員工 ID 查詢專案（ESS 我的專案）
     * 遍歷所有專案，篩選包含指定員工的成員清單
     */
    @Override
    public Page<Project> findByMemberEmployeeId(UUID employeeId, Pageable pageable) {
        List<Project> matched = findAll().stream()
                .filter(project -> project.getMembers() != null
                        && project.getMembers().stream()
                                .anyMatch(member -> employeeId.equals(member.getEmployeeId())))
                .collect(Collectors.toList());

        long total = matched.size();
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Project> content = matched.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }
}
