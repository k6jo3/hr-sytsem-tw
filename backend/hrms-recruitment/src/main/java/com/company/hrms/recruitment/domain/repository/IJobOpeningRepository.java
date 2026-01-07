package com.company.hrms.recruitment.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;

/**
 * 職缺 Repository 介面
 */
public interface IJobOpeningRepository {

    /**
     * 儲存職缺
     */
    JobOpening save(JobOpening jobOpening);

    /**
     * 刪除職缺
     */
    void delete(JobOpening jobOpening);

    /**
     * 根據 ID 查詢
     */
    Optional<JobOpening> findById(OpeningId id);

    /**
     * 分頁查詢
     */
    Page<JobOpening> findAll(QueryGroup query, Pageable pageable);
}
