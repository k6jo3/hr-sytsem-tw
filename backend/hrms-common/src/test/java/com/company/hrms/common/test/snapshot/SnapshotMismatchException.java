package com.company.hrms.common.test.snapshot;

/**
 * 快照不匹配異常
 * 當實際結果與快照不一致時拋出
 */
public class SnapshotMismatchException extends AssertionError {

    private static final long serialVersionUID = 1L;

    /** 快照名稱 */
    private final String snapshotName;

    /** 差異報告 */
    private final String diff;

    /** 預期的 JSON */
    private final String expectedJson;

    /** 實際的 JSON */
    private final String actualJson;

    public SnapshotMismatchException(String snapshotName, String diff,
                                      String expectedJson, String actualJson) {
        super(formatMessage(snapshotName, diff));
        this.snapshotName = snapshotName;
        this.diff = diff;
        this.expectedJson = expectedJson;
        this.actualJson = actualJson;
    }

    private static String formatMessage(String snapshotName, String diff) {
        return String.format(
            "快照不匹配: %s%n%s%n提示: 若為預期變更，請執行 -D%s=true 更新快照",
            snapshotName, diff, SnapshotUtils.UPDATE_SNAPSHOTS_PROPERTY);
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    public String getDiff() {
        return diff;
    }

    public String getExpectedJson() {
        return expectedJson;
    }

    public String getActualJson() {
        return actualJson;
    }
}
