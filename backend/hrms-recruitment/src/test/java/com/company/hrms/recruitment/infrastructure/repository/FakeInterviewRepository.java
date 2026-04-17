package com.company.hrms.recruitment.infrastructure.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.recruitment.domain.model.aggregate.Interview;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewId;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * IInterviewRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeInterviewRepository repo = new FakeInterviewRepository();
 * repo.seed(Interview.schedule(candidateId, ...));
 *
 * List&lt;Interview&gt; interviews = repo.findByCandidateId(candidateId);
 * assertThat(interviews).hasSize(1);
 * </pre>
 */
public class FakeInterviewRepository
        extends InMemoryBaseRepository<Interview, InterviewId>
        implements IInterviewRepository {

    public FakeInterviewRepository() {
        super(Interview.class, Interview::getId);
    }

    /**
     * 儲存面試（介面回傳實體）
     */
    @Override
    public Interview save(Interview interview) {
        return doSave(interview);
    }

    /**
     * 刪除面試
     */
    @Override
    public void delete(Interview interview) {
        super.delete(interview);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<Interview> findById(InterviewId id) {
        return super.findById(id);
    }

    /**
     * 分頁查詢
     */
    @Override
    public Page<Interview> findAll(QueryGroup query, Pageable pageable) {
        return super.findPage(query, pageable);
    }

    /**
     * 依應徵者查詢面試
     */
    @Override
    public List<Interview> findByCandidateId(CandidateId candidateId) {
        return findByField("candidateId", candidateId);
    }
}
