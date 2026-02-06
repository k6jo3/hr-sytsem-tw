package com.company.hrms.timesheet.infrastructure.repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.QueryBaseRepository;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.repository.ITimesheetEntryRepository;
import com.company.hrms.timesheet.infrastructure.entity.TimesheetEntryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class TimesheetEntryRepositoryImpl extends QueryBaseRepository<TimesheetEntryEntity, UUID>
        implements ITimesheetEntryRepository {

    public TimesheetEntryRepositoryImpl(JPAQueryFactory factory) {
        super(factory, TimesheetEntryEntity.class);
    }

    @Override
    public Page<TimesheetEntry> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable).map(this::toDomain);
    }

    @Override
    public List<TimesheetEntry> findByTimesheetId(UUID timesheetId) {
        QueryGroup query = QueryBuilder.where()
                .eq("timesheetId", timesheetId)
                .build();
        return super.findAll(query).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private TimesheetEntry toDomain(TimesheetEntryEntity entity) {
        TimesheetEntry domain = new TimesheetEntry();
        domain.setId(entity.getEntryId());
        domain.setTimesheetId(entity.getTimesheetId());
        domain.setProjectId(entity.getProjectId());
        domain.setTaskId(entity.getTaskId());
        domain.setWorkDate(entity.getWorkDate());
        domain.setHours(entity.getHours());
        domain.setDescription(entity.getDescription());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }
}
