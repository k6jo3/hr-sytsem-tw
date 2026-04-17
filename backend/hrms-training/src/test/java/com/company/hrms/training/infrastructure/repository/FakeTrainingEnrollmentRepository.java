package com.company.hrms.training.infrastructure.repository;

import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;
import com.company.hrms.training.domain.model.valueobject.EnrollmentId;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ITrainingEnrollmentRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>使用範例：
 * <pre>
 * FakeTrainingEnrollmentRepository repo = new FakeTrainingEnrollmentRepository();
 * repo.seed(TrainingEnrollment.create(...));
 *
 * boolean exists = repo.existsByCourseIdAndEmployeeId("C001", "E001");
 * assertThat(exists).isTrue();
 * </pre>
 */
public class FakeTrainingEnrollmentRepository
        extends InMemoryBaseRepository<TrainingEnrollment, EnrollmentId>
        implements ITrainingEnrollmentRepository {

    public FakeTrainingEnrollmentRepository() {
        super(TrainingEnrollment.class, TrainingEnrollment::getId);
    }

    /**
     * 儲存報名記錄（介面回傳實體）
     */
    @Override
    public TrainingEnrollment save(TrainingEnrollment enrollment) {
        return doSave(enrollment);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<TrainingEnrollment> findById(EnrollmentId id) {
        return super.findById(id);
    }

    /**
     * 檢查是否已報名過同一課程
     * 使用 stream 進行 courseId + employeeId 聯合查詢
     */
    @Override
    public boolean existsByCourseIdAndEmployeeId(String courseId, String employeeId) {
        return findAll().stream()
                .anyMatch(e -> courseId.equals(e.getCourseId())
                        && employeeId.equals(e.getEmployeeId()));
    }

    /**
     * 計算員工在指定期間內已完成的訓練時數
     * 需依據完成狀態與日期範圍過濾後加總
     *
     * 注意：此為簡化實作，真實場景需確認完成日期欄位名稱
     */
    @Override
    public BigDecimal sumCompletedHours(String employeeId, LocalDate startDate, LocalDate endDate) {
        // 簡化實作：遍歷所有報名記錄，篩選符合條件者
        // 真實場景中需依據完成狀態（COMPLETED）與完成日期過濾
        return findAll().stream()
                .filter(e -> employeeId.equals(e.getEmployeeId()))
                .filter(e -> isCompletedStatus(e))
                .map(e -> getCompletedHours(e))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 根據日期區間查詢報名記錄
     */
    @Override
    public List<TrainingEnrollment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(e -> isWithinDateRange(e, startDate, endDate))
                .collect(Collectors.toList());
    }

    // === 內部輔助方法 ===

    /**
     * 判斷報名記錄是否為已完成狀態
     */
    private boolean isCompletedStatus(TrainingEnrollment enrollment) {
        try {
            var status = enrollment.getStatus();
            return status != null && "COMPLETED".equals(status.name());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 取得已完成時數（簡化實作，回傳 0）
     * TODO: 需根據實際 aggregate 欄位調整
     */
    private BigDecimal getCompletedHours(TrainingEnrollment enrollment) {
        try {
            var field = TrainingEnrollment.class.getDeclaredField("completedHours");
            field.setAccessible(true);
            BigDecimal hours = (BigDecimal) field.get(enrollment);
            return hours != null ? hours : BigDecimal.ZERO;
        } catch (NoSuchFieldException e) {
            // 如果沒有 completedHours 欄位，回傳 0
            return BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 判斷報名記錄是否在指定日期範圍內
     * TODO: 需根據實際 aggregate 的日期欄位名稱調整
     */
    private boolean isWithinDateRange(TrainingEnrollment enrollment, LocalDate startDate, LocalDate endDate) {
        try {
            var field = TrainingEnrollment.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            Object dateValue = field.get(enrollment);
            if (dateValue instanceof java.time.LocalDateTime) {
                LocalDate date = ((java.time.LocalDateTime) dateValue).toLocalDate();
                return !date.isBefore(startDate) && !date.isAfter(endDate);
            }
            return true; // 無法判斷時預設包含
        } catch (Exception e) {
            return true;
        }
    }
}
