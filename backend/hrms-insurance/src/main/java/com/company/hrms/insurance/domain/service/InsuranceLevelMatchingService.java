package com.company.hrms.insurance.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IInsuranceLevelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 投保級距對應Domain Service
 * 根據月薪找到適當的投保級距
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InsuranceLevelMatchingService {

    private final IInsuranceLevelRepository levelRepository;

    /**
     * 根據月薪找到適當的投保級距
     * 
     * @param monthlySalary 月薪
     * @param type          保險類型
     * @param referenceDate 參考日期 (用於判斷級距是否有效)
     */
    public Optional<InsuranceLevel> findAppropriateLevel(
            BigDecimal monthlySalary,
            InsuranceType type,
            LocalDate referenceDate) {

        if (monthlySalary == null || monthlySalary.compareTo(BigDecimal.ZERO) <= 0) {
            return Optional.empty();
        }

        // 取得該保險類型的所有有效級距（轉為可變 List 以便排序）
        List<InsuranceLevel> levels = new ArrayList<>(levelRepository.findByTypeAndActiveOn(type, referenceDate));

        if (levels.isEmpty()) {
            return Optional.empty();
        }

        // 依投保金額由小到大排序
        levels.sort(Comparator.comparing(InsuranceLevel::getMonthlySalary));

        // 找到 <= 月薪的最高級距
        InsuranceLevel matchedLevel = levels.get(0); // 預設最低級距

        for (InsuranceLevel level : levels) {
            if (level.getMonthlySalary().compareTo(monthlySalary) <= 0) {
                matchedLevel = level;
            } else {
                break;
            }
        }

        return Optional.of(matchedLevel);
    }

    /**
     * 使用今天作為參考日期
     */
    public Optional<InsuranceLevel> findAppropriateLevel(BigDecimal monthlySalary, InsuranceType type) {
        return findAppropriateLevel(monthlySalary, type, LocalDate.now());
    }

    /**
     * 根據員工 ID 重新匹配投保級距
     *
     * <p>當薪資結構變更時，由事件消費者呼叫此方法，
     * 根據員工最新薪資重新計算對應的勞健保投保級距。
     *
     * TODO: 實作完整邏輯 — 需查詢員工目前投保記錄與最新薪資，
     *       比對級距差異後決定是否調整，並更新投保紀錄。
     *       預計需要注入 IInsuranceEnrollmentRepository 及跨服務查詢薪資。
     *
     * @param employeeId 員工 ID
     */
    public void recalculateLevelForEmployee(String employeeId) {
        log.info("[InsuranceLevelMatchingService] 收到級距重算請求 — 員工ID: {}", employeeId);

        // TODO: 實作完整的級距重算邏輯
        // 1. 查詢員工目前的投保紀錄（勞保 + 健保）
        // 2. 取得員工最新月薪（可能需跨服務查詢 Payroll，或從事件中取得）
        // 3. 呼叫 findAppropriateLevel() 找到新級距
        // 4. 呼叫 shouldAdjustLevel() 判斷是否需要調整
        // 5. 如需調整，更新投保紀錄並發布 InsuranceLevelAdjustedEvent

        log.warn("[InsuranceLevelMatchingService] 級距重算方法尚未完整實作，員工ID: {}", employeeId);
    }

    /**
     * 檢查是否需要調整級距
     * 
     * @param currentLevel 目前級距
     * @param newSalary    新月薪
     * @return 是否需要調整 (差異 >= 2 級視為強制調整)
     */
    public boolean shouldAdjustLevel(InsuranceLevel currentLevel, BigDecimal newSalary, InsuranceType type) {
        Optional<InsuranceLevel> newLevel = findAppropriateLevel(newSalary, type);
        if (newLevel.isEmpty()) {
            return false;
        }

        int levelDifference = Math.abs(currentLevel.getLevelNumber() - newLevel.get().getLevelNumber());
        return levelDifference >= 2;
    }
}
