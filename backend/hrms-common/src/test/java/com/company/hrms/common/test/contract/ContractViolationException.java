package com.company.hrms.common.test.contract;

import java.util.List;

/**
 * 合約違規例外
 * 當 QueryGroup 未符合 Markdown 合約定義時拋出
 */
public class ContractViolationException extends AssertionError {

    private static final long serialVersionUID = 1L;

    /** 場景 ID */
    private final String scenarioId;

    /** 缺失的過濾條件 */
    private final List<String> missingFilters;

    /** 實際的過濾條件 */
    private final List<String> actualFilters;

    public ContractViolationException(String message) {
        super(message);
        this.scenarioId = null;
        this.missingFilters = null;
        this.actualFilters = null;
    }

    public ContractViolationException(String scenarioId, String missingFilter, List<String> actualFilters) {
        super(formatMessage(scenarioId, missingFilter, actualFilters));
        this.scenarioId = scenarioId;
        this.missingFilters = List.of(missingFilter);
        this.actualFilters = actualFilters;
    }

    public ContractViolationException(String scenarioId, List<String> missingFilters, List<String> actualFilters) {
        super(formatMessage(scenarioId, missingFilters, actualFilters));
        this.scenarioId = scenarioId;
        this.missingFilters = missingFilters;
        this.actualFilters = actualFilters;
    }

    private static String formatMessage(String scenarioId, String missingFilter, List<String> actualFilters) {
        return formatMessage(scenarioId, List.of(missingFilter), actualFilters);
    }

    private static String formatMessage(String scenarioId, List<String> missingFilters, List<String> actualFilters) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    合約驗證失敗                               ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append(String.format("║ 場景 ID: %-52s ║%n", scenarioId));
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║ 缺失的過濾條件:                                               ║\n");

        for (String filter : missingFilters) {
            sb.append(String.format("║   ❌ %-56s ║%n", filter));
        }

        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║ 實際產出的過濾條件:                                           ║\n");

        if (actualFilters == null || actualFilters.isEmpty()) {
            sb.append("║   (無)                                                        ║\n");
        } else {
            for (String filter : actualFilters) {
                sb.append(String.format("║   ✓ %-56s ║%n", truncate(filter, 56)));
            }
        }

        sb.append("╚══════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    private static String truncate(String str, int maxLen) {
        if (str == null) return "";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }

    public String getScenarioId() {
        return scenarioId;
    }

    public List<String> getMissingFilters() {
        return missingFilters;
    }

    public List<String> getActualFilters() {
        return actualFilters;
    }
}
