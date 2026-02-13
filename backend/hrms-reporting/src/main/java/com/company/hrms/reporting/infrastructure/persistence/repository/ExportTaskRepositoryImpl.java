package com.company.hrms.reporting.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.reporting.domain.model.export.ExportTask;
import com.company.hrms.reporting.domain.model.export.ExportTaskId;
import com.company.hrms.reporting.domain.repository.IExportTaskRepository;
import com.company.hrms.reporting.infrastructure.persistence.mapper.ExportTaskMapper;
import com.company.hrms.reporting.infrastructure.persistence.po.ExportTaskPO;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExportTaskRepositoryImpl implements IExportTaskRepository {

    private final EntityManager entityManager;

    @Override
    public ExportTask save(ExportTask task) {
        ExportTaskPO po = ExportTaskMapper.toPO(task);
        entityManager.merge(po);
        entityManager.flush();
        return task;
    }

    @Override
    public Optional<ExportTask> findById(ExportTaskId id) {
        ExportTaskPO po = entityManager.find(ExportTaskPO.class, id.getValue());
        return Optional.ofNullable(ExportTaskMapper.toDomain(po));
    }

    @Override
    public void delete(ExportTaskId id) {
        ExportTaskPO po = entityManager.find(ExportTaskPO.class, id.getValue());
        if (po != null) {
            entityManager.remove(po);
            entityManager.flush();
        }
    }
}
