import type {
    CycleStatus,
    CycleType,
    EvaluationItemDto,
    EvaluationTemplateDto,
    MyPerformanceDto,
    PerformanceCycleDto,
    PerformanceRating,
    PerformanceReviewDto,
    ReviewStatus,
    ReviewType,
    TeamReviewItemDto,
} from '../api/PerformanceTypes';
import type {
    EvaluationItemViewModel,
    EvaluationTemplateViewModel,
    MyPerformanceViewModel,
    PerformanceCycleViewModel,
    PerformanceReviewViewModel,
    TeamReviewItemViewModel,
} from '../model/PerformanceViewModel';

/**
 * Performance ViewModel Factory (績效管理視圖模型工廠)
 * 將 API DTO 轉換為前端顯示用的 ViewModel
 */
export class PerformanceViewModelFactory {
  /**
   * 將 PerformanceCycleDto 轉換為 PerformanceCycleViewModel
   */
  static createCycleViewModel(dto: PerformanceCycleDto): PerformanceCycleViewModel {
    return {
      cycleId: dto.cycle_id,
      cycleName: dto.cycle_name,
      cycleTypeLabel: this.mapCycleTypeLabel(dto.cycle_type),
      cycleTypeColor: this.mapCycleTypeColor(dto.cycle_type),
      periodDisplay: `${dto.start_date} ~ ${dto.end_date}`,
      selfEvalDeadlineDisplay: dto.self_eval_deadline,
      managerEvalDeadlineDisplay: dto.manager_eval_deadline,
      statusLabel: this.mapCycleStatusLabel(dto.status),
      statusColor: this.mapCycleStatusColor(dto.status),
      isInProgress: dto.status === 'IN_PROGRESS',
      isDraft: dto.status === 'DRAFT',
      isCompleted: dto.status === 'COMPLETED',
      daysRemaining: dto.self_eval_deadline ? this.calculateDaysRemaining(dto.self_eval_deadline) : undefined,
      daysRemainingDisplay: dto.self_eval_deadline ? this.formatDaysRemaining(dto.self_eval_deadline) : undefined,
    };
  }

  /**
   * 將 EvaluationTemplateDto 轉換為 EvaluationTemplateViewModel
   */
  static createTemplateViewModel(dto: EvaluationTemplateDto): EvaluationTemplateViewModel {
    const evaluationItems = dto.evaluation_items.map((item) =>
      this.createEvaluationItemViewModel(item)
    );

    const totalWeight = evaluationItems.reduce((sum, item) => sum + item.weight, 0);

    return {
      formName: dto.form_name,
      scoringSystem: this.mapScoringSystemLabel(dto.scoring_system),
      scoringSystemValue: dto.scoring_system,
      forcedDistribution: dto.forced_distribution,
      forcedDistributionLabel: dto.forced_distribution ? '啟用' : '停用',
      distributionRules: dto.distribution_rules,
      evaluationItems,
      totalWeight: Math.round(totalWeight * 100),
      isValid: Math.round(totalWeight * 100) === 100,
    };
  }

  /**
   * 將 EvaluationItemDto 轉換為 EvaluationItemViewModel
   */
  static createEvaluationItemViewModel(dto: EvaluationItemDto): EvaluationItemViewModel {
    const hasScore = dto.score != null;
    const weightedScore = hasScore ? dto.weight * dto.score! : undefined;

    return {
      itemId: dto.item_id,
      itemName: dto.item_name,
      weight: dto.weight,
      weightDisplay: `${Math.round(dto.weight * 100)}%`,
      score: dto.score,
      scoreDisplay: hasScore ? dto.score!.toString() : '-',
      comments: dto.comments,
      maxScore: dto.max_score,
      isRequired: true,
      weightedScore,
      weightedScoreDisplay: weightedScore !== undefined ? weightedScore.toFixed(1) : undefined,
    };
  }

  /**
   * 將 PerformanceReviewDto 轉換為 PerformanceReviewViewModel
   */
  static createReviewViewModel(dto: PerformanceReviewDto): PerformanceReviewViewModel {
    const evaluationItems = dto.evaluation_items.map((item) =>
      this.createEvaluationItemViewModel(item)
    );

    return {
      reviewId: dto.review_id,
      cycleName: dto.cycle_name,
      employeeName: dto.employee_name,
      reviewerName: dto.reviewer_name,
      reviewTypeLabel: this.mapReviewTypeLabel(dto.review_type),
      evaluationItems,
      overallScore: dto.overall_score,
      overallScoreDisplay: dto.overall_score !== undefined ? dto.overall_score.toFixed(1) : '-',
      overallRating: dto.overall_rating,
      overallRatingDisplay: dto.overall_rating ? this.mapRatingDisplay(dto.overall_rating) : '-',
      overallRatingColor: dto.overall_rating ? this.mapRatingColor(dto.overall_rating) : 'default',
      comments: dto.comments,
      statusLabel: this.mapReviewStatusLabel(dto.status),
      statusColor: this.mapReviewStatusColor(dto.status),
      isDraft: dto.status === 'DRAFT',
      isSubmitted: dto.status === 'SUBMITTED',
      isFinalized: dto.status === 'FINALIZED',
      canEdit: dto.status === 'DRAFT',
      canSubmit: dto.status === 'DRAFT' && evaluationItems.length > 0,
      submittedAtDisplay: dto.submitted_at,
      createdAtDisplay: dto.created_at,
    };
  }

  /**
   * 將 MyPerformanceDto 轉換為 MyPerformanceViewModel
   */
  static createMyPerformanceViewModel(dto: MyPerformanceDto): MyPerformanceViewModel {
    const currentCycle = dto.current_cycle
      ? this.createCycleViewModel(dto.current_cycle)
      : undefined;

    const selfReview = dto.self_review
      ? this.createReviewViewModel(dto.self_review)
      : undefined;

    const managerReview = dto.manager_review
      ? this.createReviewViewModel(dto.manager_review)
      : undefined;

    const history = dto.history.map((review) => this.createReviewViewModel(review));

    const hasCurrentCycle = !!dto.current_cycle;

    let statusMessage = '';
    let statusType: 'success' | 'warning' | 'info' | 'error' = 'info';
    let nextAction: string | undefined;

    if (!hasCurrentCycle) {
      statusMessage = '目前無進行中的考核週期';
      statusType = 'warning';
    } else if (selfReview?.isDraft) {
      statusMessage = '🔵 自評進行中';
      statusType = 'info';
      nextAction = '請完成自我評估並提交';
    } else if (selfReview?.isSubmitted && !managerReview) {
      statusMessage = '⏳ 等待主管評核';
      statusType = 'info';
      nextAction = '已提交自評，等待主管評核';
    } else if (managerReview?.isSubmitted) {
      statusMessage = '✅ 考核完成';
      statusType = 'success';
      nextAction = '查看主管評核結果';
    }

    return {
      currentCycle,
      selfReview,
      managerReview,
      history,
      hasCurrentCycle,
      canSubmitSelfEval: dto.can_submit_self_eval,
      statusMessage,
      statusType,
      nextAction,
    };
  }

  /**
   * 計算總分
   */
  static calculateOverallScore(items: EvaluationItemDto[]): number {
    if (items.length === 0) return 0;

    const totalScore = items.reduce((sum, item) => {
      if (item.score !== undefined) {
        return sum + item.weight * item.score;
      }
      return sum;
    }, 0);

    return Math.round(totalScore * 10) / 10; // Round to 1 decimal place
  }

  /**
   * Mapping functions
   */
  private static mapCycleTypeLabel(type: CycleType): string {
    const labelMap: Record<CycleType, string> = {
      PROBATION: '試用期考核',
      QUARTERLY: '季度考核',
      ANNUAL: '年度考核',
    };
    return labelMap[type];
  }

  private static mapCycleTypeColor(type: CycleType): string {
    const colorMap: Record<CycleType, string> = {
      PROBATION: 'orange',
      QUARTERLY: 'cyan',
      ANNUAL: 'blue',
    };
    return colorMap[type];
  }

  private static mapCycleStatusLabel(status: CycleStatus): string {
    const labelMap: Record<CycleStatus, string> = {
      DRAFT: '草稿',
      ACTIVE: '進行中',
      IN_PROGRESS: '進行中',
      COMPLETED: '已完成',
      CLOSED: '已結案',
    };
    return labelMap[status];
  }

  private static mapCycleStatusColor(status: CycleStatus): string {
    const colorMap: Record<CycleStatus, string> = {
      DRAFT: 'default',
      ACTIVE: 'processing',
      IN_PROGRESS: 'processing',
      COMPLETED: 'success',
      CLOSED: 'default',
    };
    return colorMap[status];
  }

  private static mapReviewTypeLabel(type: ReviewType): string {
    const labelMap: Record<ReviewType, string> = {
      SELF: '自評',
      MANAGER: '主管評',
      PEER: '同儕評',
    };
    return labelMap[type];
  }

  private static mapReviewStatusLabel(status: ReviewStatus): string {
    const labelMap: Record<ReviewStatus, string> = {
      DRAFT: '草稿',
      SUBMITTED: '已提交',
      FINALIZED: '已定案',
    };
    return labelMap[status];
  }

  private static mapReviewStatusColor(status: ReviewStatus): string {
    const colorMap: Record<ReviewStatus, string> = {
      DRAFT: 'default',
      SUBMITTED: 'processing',
      FINALIZED: 'success',
    };
    return colorMap[status];
  }

  private static mapRatingDisplay(rating: PerformanceRating): string {
    const displayMap: Record<PerformanceRating, string> = {
      A: 'A (優秀)',
      B: 'B (良好)',
      C: 'C (待改進)',
      D: 'D (不合格)',
    };
    return displayMap[rating];
  }

  private static mapRatingColor(rating: PerformanceRating): string {
    const colorMap: Record<PerformanceRating, string> = {
      A: 'success',
      B: 'processing',
      C: 'warning',
      D: 'error',
    };
    return colorMap[rating];
  }

  private static calculateDaysRemaining(deadline: string): number {
    const deadlineDate = new Date(deadline);
    const today = new Date();
    const diffTime = deadlineDate.getTime() - today.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  private static formatDaysRemaining(deadline: string): string {
    const days = this.calculateDaysRemaining(deadline);
    if (days < 0) return '已逾期';
    if (days === 0) return '今天截止';
    return `剩餘 ${days} 天`;
  }

  /**
   * 將 TeamReviewItemDto 轉換為 TeamReviewItemViewModel
   */
  static createTeamReviewItemViewModel(dto: TeamReviewItemDto): TeamReviewItemViewModel {
      let completionRate = 0;
      if (dto.manager_review_status === 'FINALIZED') completionRate = 100;
      else if (dto.manager_review_status === 'SUBMITTED') completionRate = 90;
      else if (dto.self_review_status === 'SUBMITTED') completionRate = 60;
      else if (dto.self_review_status === 'DRAFT') completionRate = 20;

    return {
      employeeId: dto.employee_id,
      employeeName: dto.employee_name,
      employeeCode: dto.employee_code,
      departmentName: dto.department_name,
      positionName: dto.position_name,
      selfReviewStatusLabel: this.mapReviewStatusLabel(dto.self_review_status),
      selfReviewStatusColor: this.mapReviewStatusColor(dto.self_review_status),
      managerReviewStatusLabel: this.mapReviewStatusLabel(dto.manager_review_status),
      managerReviewStatusColor: this.mapReviewStatusColor(dto.manager_review_status),
      overallScoreDisplay: dto.overall_score !== undefined ? dto.overall_score.toFixed(1) : '-',
      overallRating: dto.overall_rating ? this.mapRatingDisplay(dto.overall_rating).split(' ')[0] : undefined, // simplified
      overallRatingColor: dto.overall_rating ? this.mapRatingColor(dto.overall_rating) : undefined,
      selfSubmittedAtDisplay: dto.self_submitted_at,
      managerSubmittedAtDisplay: dto.manager_submitted_at,
      needsManagerReview: dto.self_review_status === 'SUBMITTED' && dto.manager_review_status !== 'SUBMITTED' && dto.manager_review_status !== 'FINALIZED',
      completionRate,
    };
  }

  private static mapScoringSystemLabel(system: string): string {
    const labelMap: Record<string, string> = {
      FIVE_POINT: '五分制',
      FIVE_LEVEL: '五等第',
      PERCENTAGE: '百分制',
    };
    return labelMap[system] || system;
  }
}
