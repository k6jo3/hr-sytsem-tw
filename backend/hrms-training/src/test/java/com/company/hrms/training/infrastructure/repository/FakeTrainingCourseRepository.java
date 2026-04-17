package com.company.hrms.training.infrastructure.repository;

import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

import java.util.Optional;

/**
 * ITrainingCourseRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeTrainingCourseRepository repo = new FakeTrainingCourseRepository();
 * repo.seed(TrainingCourse.create(...));
 *
 * boolean exists = repo.existsByCourseCode("TC001");
 * assertThat(exists).isTrue();
 * </pre>
 */
public class FakeTrainingCourseRepository
        extends InMemoryBaseRepository<TrainingCourse, CourseId>
        implements ITrainingCourseRepository {

    public FakeTrainingCourseRepository() {
        super(TrainingCourse.class, TrainingCourse::getId);
    }

    /**
     * 儲存課程（介面回傳實體）
     */
    @Override
    public TrainingCourse save(TrainingCourse course) {
        return doSave(course);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<TrainingCourse> findById(CourseId id) {
        return super.findById(id);
    }

    /**
     * 檢查課程代碼是否存在
     */
    @Override
    public boolean existsByCourseCode(String courseCode) {
        return existsByField("courseCode", courseCode);
    }
}
