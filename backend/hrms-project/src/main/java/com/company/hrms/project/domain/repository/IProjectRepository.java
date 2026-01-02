package com.company.hrms.project.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.valueobject.ProjectId;

public interface IProjectRepository {
    Project save(Project project);

    Page<Project> findProjects(QueryGroup query, Pageable pageable);

    Optional<Project> findById(ProjectId id);

    List<Project> findAll();

    boolean existsById(ProjectId id);

    void deleteById(ProjectId id);

    /**
     * 根據成員員工 ID 查詢專案 (ESS 我的專案)
     */
    Page<Project> findByMemberEmployeeId(UUID employeeId, Pageable pageable);
}
