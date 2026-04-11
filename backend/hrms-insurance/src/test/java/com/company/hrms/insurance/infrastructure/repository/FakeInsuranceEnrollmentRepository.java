package com.company.hrms.insurance.infrastructure.repository;

import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IInsuranceEnrollmentRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>注意：InsuranceEnrollment 不繼承 AggregateRoot，而是純 POJO，
 * 但具有 getId() 方法回傳 EnrollmentId。
 *
 * <p>使用範例：
 * <pre>
 * FakeInsuranceEnrollmentRepository repo = new FakeInsuranceEnrollmentRepository();
 * repo.seed(new InsuranceEnrollment(enrollmentId, "EMP001", unitId, InsuranceType.LABOR, ...));
 *
 * List&lt;InsuranceEnrollment&gt; records = repo.findByEmployeeId("EMP001");
 * assertThat(records).hasSize(1);
 * </pre>
 */
public class FakeInsuranceEnrollmentRepository
        extends InMemoryBaseRepository<InsuranceEnrollment, EnrollmentId>
        implements IInsuranceEnrollmentRepository {

    public FakeInsuranceEnrollmentRepository() {
        super(InsuranceEnrollment.class, InsuranceEnrollment::getId);
    }

    /**
     * 新增加退保記錄（介面回傳實體）
     */
    @Override
    public InsuranceEnrollment save(InsuranceEnrollment enrollment) {
        return doSave(enrollment);
    }

    /**
     * 更新加退保記錄（介面回傳實體）
     */
    @Override
    public InsuranceEnrollment update(InsuranceEnrollment enrollment) {
        doSave(enrollment);
        return enrollment;
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<InsuranceEnrollment> findById(EnrollmentId id) {
        return super.findById(id);
    }

    /**
     * 根據員工 ID 查詢所有加退保記錄
     */
    @Override
    public List<InsuranceEnrollment> findByEmployeeId(String employeeId) {
        return findByField("employeeId", employeeId);
    }

    /**
     * 根據員工 ID 和保險類型查詢有效的加保記錄
     * 使用 stream 進行 employeeId + insuranceType + status 聯合查詢
     */
    @Override
    public Optional<InsuranceEnrollment> findActiveByEmployeeIdAndType(String employeeId, InsuranceType type) {
        return findAll().stream()
                .filter(e -> employeeId.equals(getFieldValue(e, "employeeId")))
                .filter(e -> type.equals(getFieldValue(e, "insuranceType")))
                .filter(e -> isActiveStatus(e))
                .findFirst();
    }

    /**
     * 根據員工 ID 查詢所有有效的加保記錄
     */
    @Override
    public List<InsuranceEnrollment> findAllActiveByEmployeeId(String employeeId) {
        return findAll().stream()
                .filter(e -> employeeId.equals(getFieldValue(e, "employeeId")))
                .filter(e -> isActiveStatus(e))
                .collect(Collectors.toList());
    }

    /**
     * 根據日期區間查詢加退保記錄（用於申報檔匯出）
     */
    @Override
    public List<InsuranceEnrollment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return findAll().stream()
                .filter(e -> {
                    LocalDate enrollDate = getEnrollDate(e);
                    if (enrollDate == null) return false;
                    return !enrollDate.isBefore(startDate) && !enrollDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    /**
     * 查詢所有加退保記錄
     */
    @Override
    public List<InsuranceEnrollment> findAll() {
        return super.findAll();
    }

    // === 內部輔助方法 ===

    /**
     * 判斷是否為有效狀態（ACTIVE）
     */
    private boolean isActiveStatus(InsuranceEnrollment enrollment) {
        Object status = getFieldValue(enrollment, "status");
        return status != null && "ACTIVE".equals(status.toString());
    }

    /**
     * 取得加保日期
     */
    private LocalDate getEnrollDate(InsuranceEnrollment enrollment) {
        Object value = getFieldValue(enrollment, "enrollDate");
        return value instanceof LocalDate ? (LocalDate) value : null;
    }

    /**
     * 透過反射取得欄位值
     */
    private Object getFieldValue(InsuranceEnrollment enrollment, String fieldName) {
        try {
            var field = InsuranceEnrollment.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(enrollment);
        } catch (Exception e) {
            throw new RuntimeException("無法取得欄位: " + fieldName, e);
        }
    }
}
