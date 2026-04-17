package com.company.hrms.insurance.infrastructure.repository;

import com.company.hrms.common.test.inmemory.InMemoryBaseRepository;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.model.valueobject.LevelId;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * IInsuranceLevelRepository 的 InMemory 實作
 * 用於測試，不需 Spring Context 或資料庫
 *
 * <p>注意：InsuranceLevel 不繼承 AggregateRoot，而是純 POJO，
 * 但具有 getId() 方法回傳 LevelId。
 *
 * <p>使用範例：
 * <pre>
 * FakeInsuranceLevelRepository repo = new FakeInsuranceLevelRepository();
 * repo.seed(new InsuranceLevel(levelId, InsuranceType.LABOR, 1, salary, effectiveDate));
 *
 * List&lt;InsuranceLevel&gt; levels = repo.findByType(InsuranceType.LABOR);
 * assertThat(levels).isNotEmpty();
 * </pre>
 */
public class FakeInsuranceLevelRepository
        extends InMemoryBaseRepository<InsuranceLevel, LevelId>
        implements IInsuranceLevelRepository {

    public FakeInsuranceLevelRepository() {
        super(InsuranceLevel.class, InsuranceLevel::getId);
    }

    /**
     * 根據 ID 查詢
     */
    @Override
    public Optional<InsuranceLevel> findById(LevelId id) {
        return super.findById(id);
    }

    /**
     * 根據保險類型查詢所有有效級距
     */
    @Override
    public List<InsuranceLevel> findByType(InsuranceType type) {
        return findByField("insuranceType", type);
    }

    /**
     * 根據保險類型和指定日期查詢有效級距
     * 篩選 effectiveDate <= date 且 (endDate is null 或 endDate >= date)
     */
    @Override
    public List<InsuranceLevel> findByTypeAndActiveOn(InsuranceType type, LocalDate date) {
        return findAll().stream()
                .filter(level -> type.equals(getFieldValue(level, "insuranceType")))
                .filter(level -> {
                    LocalDate effectiveDate = (LocalDate) getFieldValue(level, "effectiveDate");
                    LocalDate endDate = (LocalDate) getFieldValue(level, "endDate");
                    if (effectiveDate == null || effectiveDate.isAfter(date)) return false;
                    return endDate == null || !endDate.isBefore(date);
                })
                .collect(Collectors.toList());
    }

    /**
     * 根據保險類型和級距號碼查詢
     */
    @Override
    public Optional<InsuranceLevel> findByTypeAndLevelNumber(InsuranceType type, int levelNumber) {
        return findAll().stream()
                .filter(level -> type.equals(getFieldValue(level, "insuranceType")))
                .filter(level -> {
                    Object num = getFieldValue(level, "levelNumber");
                    return num instanceof Integer && (Integer) num == levelNumber;
                })
                .findFirst();
    }

    /**
     * 查詢指定日期有效的所有級距
     */
    @Override
    public List<InsuranceLevel> findAllActive(LocalDate date) {
        return findAll().stream()
                .filter(level -> {
                    LocalDate effectiveDate = (LocalDate) getFieldValue(level, "effectiveDate");
                    LocalDate endDate = (LocalDate) getFieldValue(level, "endDate");
                    if (effectiveDate == null || effectiveDate.isAfter(date)) return false;
                    return endDate == null || !endDate.isBefore(date);
                })
                .collect(Collectors.toList());
    }

    /**
     * 查詢指定保險類型中有效且無結束日期的級距
     */
    @Override
    public List<InsuranceLevel> findByTypeAndEndDateIsNull(InsuranceType type) {
        return findAll().stream()
                .filter(level -> type.equals(getFieldValue(level, "insuranceType")))
                .filter(level -> getFieldValue(level, "endDate") == null)
                .collect(Collectors.toList());
    }

    /**
     * 新增投保級距（介面回傳實體）
     */
    @Override
    public InsuranceLevel save(InsuranceLevel level) {
        return doSave(level);
    }

    /**
     * 更新投保級距（介面回傳實體）
     */
    @Override
    public InsuranceLevel update(InsuranceLevel level) {
        doSave(level);
        return level;
    }

    /**
     * 批量新增投保級距
     */
    @Override
    public void saveBatch(List<InsuranceLevel> levels) {
        levels.forEach(this::save);
    }

    // === 內部輔助方法 ===

    /**
     * 透過反射取得欄位值
     */
    private Object getFieldValue(InsuranceLevel level, String fieldName) {
        try {
            var field = InsuranceLevel.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(level);
        } catch (Exception e) {
            throw new RuntimeException("無法取得欄位: " + fieldName, e);
        }
    }
}
