/**
 * Performance ViewModel (績效管理視圖模型)
 * 前端顯示用的資料模型
 */

/**
 * 評估項目 ViewModel
 */
export interface EvaluationItemViewModel {
  itemId: string;
  itemName: string;
  weight: number; // 權重 (0.0 - 1.0)
  weightDisplay: string; // "30%"
  score?: number; // 分數 (1-5)
  scoreDisplay: string; // "4" or "-"
  comments?: string;
  maxScore: number;
  isRequired: boolean; // 是否必填
  weightedScore?: number; // 該項加權分數
  weightedScoreDisplay?: string; // "1.2"
}

/**
 * 考核週期 ViewModel
 */
export interface PerformanceCycleViewModel {
  cycleId: string;
  cycleName: string;
  cycleTypeLabel: string;
  cycleTypeColor: string;
  periodDisplay: string; // "2025/01/01 - 2025/12/31"
  selfEvalDeadlineDisplay?: string;
  managerEvalDeadlineDisplay?: string;
  statusLabel: string;
  statusColor: string;
  isInProgress: boolean;
  isDraft: boolean;
  isCompleted: boolean;
  daysRemaining?: number;
  daysRemainingDisplay?: string;
}

/**
 * 考核記錄 ViewModel
 */
export interface PerformanceReviewViewModel {
  reviewId: string;
  cycleName: string;
  employeeName: string;
  reviewerName: string;
  reviewTypeLabel: string;
  evaluationItems: EvaluationItemViewModel[];
  overallScore?: number;
  overallScoreDisplay: string; // "4.1" or "-"
  overallRating?: string; // "A", "B", "C", "D"
  overallRatingDisplay: string; // "A (優秀)" or "-"
  overallRatingColor: string;
  comments?: string;
  statusLabel: string;
  statusColor: string;
  isDraft: boolean;
  isSubmitted: boolean;
  isFinalized: boolean;
  canEdit: boolean;
  canSubmit: boolean;
  submittedAtDisplay?: string;
  createdAtDisplay: string;
}

/**
 * 我的考核 ViewModel
 */
export interface MyPerformanceViewModel {
  currentCycle?: PerformanceCycleViewModel;
  selfReview?: PerformanceReviewViewModel;
  managerReview?: PerformanceReviewViewModel;
  history: PerformanceReviewViewModel[];
  hasCurrentCycle: boolean;
  canSubmitSelfEval: boolean;
  statusMessage: string;
  statusType: 'success' | 'warning' | 'info' | 'error';
  nextAction?: string; // "請完成自我評估" | "等待主管評核"
}

/**
 * 團隊考核列表項 ViewModel
 */
export interface TeamReviewItemViewModel {
  employeeId: string;
  employeeName: string;
  employeeCode: string;
  departmentName: string;
  positionName: string;
  selfReviewStatusLabel: string;
  selfReviewStatusColor: string;
  managerReviewStatusLabel: string;
  managerReviewStatusColor: string;
  overallScoreDisplay?: string;
  overallRating?: string;
  overallRatingColor?: string;
  selfSubmittedAtDisplay?: string;
  managerSubmittedAtDisplay?: string;
  needsManagerReview: boolean;
  completionRate: number; // 0-100
}

/**
 * 績效分佈 ViewModel
 */
export interface PerformanceDistributionViewModel {
  rating: string; // "A", "B", "C", "D"
  ratingLabel: string; // "A (優秀)"
  ratingColor: string;
  count: number;
  percentage: number;
  percentageDisplay: string; // "25.5%"
}

/**
 * 考核表單模板 ViewModel
 */
export interface EvaluationTemplateViewModel {
  formName: string;
  scoringSystem: string; // "五分制" | "五等第" | "百分制"
  scoringSystemValue: 'FIVE_POINT' | 'FIVE_LEVEL' | 'PERCENTAGE';
  forcedDistribution: boolean;
  forcedDistributionLabel: string; // "啟用" | "停用"
  distributionRules?: Record<string, number>;
  evaluationItems: EvaluationItemViewModel[];
  totalWeight: number; // should always be 100
  isValid: boolean;
}
