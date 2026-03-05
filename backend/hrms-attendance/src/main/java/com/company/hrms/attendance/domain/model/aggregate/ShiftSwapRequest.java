package com.company.hrms.attendance.domain.model.aggregate;

import java.time.LocalDate;

import com.company.hrms.attendance.domain.model.valueobject.SwapRequestId;
import com.company.hrms.attendance.domain.model.valueobject.SwapStatus;
import com.company.hrms.common.domain.model.AggregateRoot;

import lombok.Getter;

/**
 * 換班申請聚合根
 * 員工 A 與員工 B 互換某日班別
 */
@Getter
public class ShiftSwapRequest extends AggregateRoot<SwapRequestId> {

    private String requesterId;       // 申請人
    private String counterpartId;     // 交換對象
    private LocalDate requesterDate;  // 申請人原排班日期
    private LocalDate counterpartDate; // 對方原排班日期
    private String requesterShiftId;  // 申請人原班別
    private String counterpartShiftId; // 對方原班別
    private SwapStatus status;
    private String reason;
    private String rejectionReason;
    private String approverId;
    private boolean isDeleted;

    public ShiftSwapRequest(SwapRequestId id, String requesterId, String counterpartId,
            LocalDate requesterDate, LocalDate counterpartDate,
            String requesterShiftId, String counterpartShiftId, String reason) {
        super(id);
        validate(requesterId, counterpartId, requesterDate, counterpartDate);
        this.requesterId = requesterId;
        this.counterpartId = counterpartId;
        this.requesterDate = requesterDate;
        this.counterpartDate = counterpartDate;
        this.requesterShiftId = requesterShiftId;
        this.counterpartShiftId = counterpartShiftId;
        this.reason = reason;
        this.status = SwapStatus.PENDING_COUNTERPART;
        this.isDeleted = false;
    }

    /**
     * 對方同意換班
     */
    public void acceptByCounterpart() {
        if (this.status != SwapStatus.PENDING_COUNTERPART) {
            throw new IllegalStateException("僅等待對方同意時可接受");
        }
        this.status = SwapStatus.PENDING_APPROVAL;
    }

    /**
     * 對方拒絕換班
     */
    public void rejectByCounterpart(String reason) {
        if (this.status != SwapStatus.PENDING_COUNTERPART) {
            throw new IllegalStateException("僅等待對方同意時可拒絕");
        }
        this.status = SwapStatus.REJECTED;
        this.rejectionReason = reason;
    }

    /**
     * 主管核准
     */
    public void approve(String approverId) {
        if (this.status != SwapStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("僅等待審核時可核准");
        }
        this.status = SwapStatus.APPROVED;
        this.approverId = approverId;
    }

    /**
     * 主管駁回
     */
    public void reject(String approverId, String reason) {
        if (this.status != SwapStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("僅等待審核時可駁回");
        }
        this.status = SwapStatus.REJECTED;
        this.approverId = approverId;
        this.rejectionReason = reason;
    }

    /**
     * 取消申請（申請人自行取消）
     */
    public void cancel() {
        if (this.status == SwapStatus.APPROVED || this.status == SwapStatus.REJECTED) {
            throw new IllegalStateException("已結案的申請不可取消");
        }
        this.status = SwapStatus.CANCELLED;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    private void validate(String requesterId, String counterpartId,
            LocalDate requesterDate, LocalDate counterpartDate) {
        if (requesterId == null || requesterId.isBlank()) {
            throw new IllegalArgumentException("申請人 ID 不可為空");
        }
        if (counterpartId == null || counterpartId.isBlank()) {
            throw new IllegalArgumentException("交換對象 ID 不可為空");
        }
        if (requesterId.equals(counterpartId)) {
            throw new IllegalArgumentException("不可與自己換班");
        }
        if (requesterDate == null || counterpartDate == null) {
            throw new IllegalArgumentException("排班日期不可為空");
        }
    }

    /**
     * 從持久層重建
     */
    private ShiftSwapRequest(SwapRequestId id, String requesterId, String counterpartId,
            LocalDate requesterDate, LocalDate counterpartDate,
            String requesterShiftId, String counterpartShiftId,
            SwapStatus status, String reason, String rejectionReason,
            String approverId, boolean isDeleted) {
        super(id);
        this.requesterId = requesterId;
        this.counterpartId = counterpartId;
        this.requesterDate = requesterDate;
        this.counterpartDate = counterpartDate;
        this.requesterShiftId = requesterShiftId;
        this.counterpartShiftId = counterpartShiftId;
        this.status = status;
        this.reason = reason;
        this.rejectionReason = rejectionReason;
        this.approverId = approverId;
        this.isDeleted = isDeleted;
    }

    public static ShiftSwapRequest reconstitute(SwapRequestId id, String requesterId, String counterpartId,
            LocalDate requesterDate, LocalDate counterpartDate,
            String requesterShiftId, String counterpartShiftId,
            SwapStatus status, String reason, String rejectionReason,
            String approverId, boolean isDeleted) {
        return new ShiftSwapRequest(id, requesterId, counterpartId, requesterDate, counterpartDate,
                requesterShiftId, counterpartShiftId, status, reason, rejectionReason,
                approverId, isDeleted);
    }
}
