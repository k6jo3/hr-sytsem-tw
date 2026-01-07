package com.company.hrms.recruitment.domain.model.valueobject;

/**
 * 應徵者狀態
 * 
 * 狀態流程：
 * NEW → SCREENING → INTERVIEWING → OFFERED → HIRED
 * ↓ ↓ ↓
 * REJECTED REJECTED REJECTED
 */
public enum CandidateStatus {
    /**
     * 新投遞 - 剛收到履歷
     */
    NEW("新投遞"),

    /**
     * 篩選中 - 履歷篩選階段
     */
    SCREENING("篩選中"),

    /**
     * 面試中 - 進入面試流程
     */
    INTERVIEWING("面試中"),

    /**
     * 已發Offer - 已經發送錄取通知
     */
    OFFERED("已發Offer"),

    /**
     * 已錄取 - 應徵者接受Offer
     */
    HIRED("已錄取"),

    /**
     * 已拒絕 - 被拒絕或淘汰
     */
    REJECTED("已拒絕");

    private final String displayName;

    CandidateStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 檢查是否可以轉換到目標狀態
     */
    public boolean canTransitionTo(CandidateStatus targetStatus) {
        switch (this) {
            case NEW:
                return targetStatus == SCREENING || targetStatus == REJECTED;
            case SCREENING:
                return targetStatus == INTERVIEWING || targetStatus == REJECTED;
            case INTERVIEWING:
                return targetStatus == OFFERED || targetStatus == REJECTED;
            case OFFERED:
                return targetStatus == HIRED || targetStatus == REJECTED;
            case HIRED:
            case REJECTED:
                return false; // 終態不可轉換
            default:
                return false;
        }
    }
}
