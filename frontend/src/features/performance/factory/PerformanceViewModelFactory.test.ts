import { describe, it, expect } from 'vitest';
import { PerformanceViewModelFactory } from './PerformanceViewModelFactory';
import type {
  PerformanceCycleDto,
  PerformanceReviewDto,
  EvaluationItemDto,
  MyPerformanceDto,
} from '../api/PerformanceTypes';

describe('PerformanceViewModelFactory', () => {
  describe('createCycleViewModel', () => {
    it('should transform in-progress annual cycle DTO correctly', () => {
      const dto: PerformanceCycleDto = {
        cycle_id: 'cycle-001',
        cycle_name: '2025年度考核',
        cycle_type: 'ANNUAL',
        start_date: '2025-01-01',
        end_date: '2025-12-31',
        self_eval_deadline: '2026-01-15',
        manager_eval_deadline: '2026-01-31',
        status: 'IN_PROGRESS',
        created_at: '2024-12-01T00:00:00Z',
      };

      const viewModel = PerformanceViewModelFactory.createCycleViewModel(dto);

      expect(viewModel.cycleId).toBe('cycle-001');
      expect(viewModel.cycleName).toBe('2025年度考核');
      expect(viewModel.cycleTypeLabel).toBe('年度考核');
      expect(viewModel.cycleTypeColor).toBe('blue');
      expect(viewModel.periodDisplay).toBe('2025-01-01 ~ 2025-12-31');
      expect(viewModel.statusLabel).toBe('進行中');
      expect(viewModel.statusColor).toBe('processing');
      expect(viewModel.isInProgress).toBe(true);
      expect(viewModel.isDraft).toBe(false);
    });

    it('should transform probation cycle correctly', () => {
      const dto: PerformanceCycleDto = {
        cycle_id: 'cycle-002',
        cycle_name: '新人試用期考核',
        cycle_type: 'PROBATION',
        start_date: '2025-01-01',
        end_date: '2025-03-31',
        status: 'DRAFT',
        created_at: '2024-12-15T00:00:00Z',
      };

      const viewModel = PerformanceViewModelFactory.createCycleViewModel(dto);

      expect(viewModel.cycleTypeLabel).toBe('試用期考核');
      expect(viewModel.cycleTypeColor).toBe('orange');
      expect(viewModel.statusLabel).toBe('草稿');
      expect(viewModel.isDraft).toBe(true);
    });
  });

  describe('createEvaluationItemViewModel', () => {
    it('should transform evaluation item with score correctly', () => {
      const dto: EvaluationItemDto = {
        item_id: 'item-001',
        item_name: '工作品質',
        weight: 0.30,
        score: 4,
        comments: '表現優異',
        max_score: 5,
      };

      const viewModel = PerformanceViewModelFactory.createEvaluationItemViewModel(dto);

      expect(viewModel.itemId).toBe('item-001');
      expect(viewModel.itemName).toBe('工作品質');
      expect(viewModel.weight).toBe(0.30);
      expect(viewModel.weightDisplay).toBe('30%');
      expect(viewModel.score).toBe(4);
      expect(viewModel.scoreDisplay).toBe('4');
      expect(viewModel.comments).toBe('表現優異');
      expect(viewModel.weightedScore).toBe(1.2); // 0.30 * 4 = 1.2
      expect(viewModel.weightedScoreDisplay).toBe('1.2');
    });

    it('should handle item without score', () => {
      const dto: EvaluationItemDto = {
        item_id: 'item-002',
        item_name: '團隊合作',
        weight: 0.20,
        max_score: 5,
      };

      const viewModel = PerformanceViewModelFactory.createEvaluationItemViewModel(dto);

      expect(viewModel.scoreDisplay).toBe('-');
      expect(viewModel.weightedScore).toBeUndefined();
    });
  });

  describe('createReviewViewModel', () => {
    it('should transform draft self-review DTO correctly', () => {
      const dto: PerformanceReviewDto = {
        review_id: 'review-001',
        cycle_id: 'cycle-001',
        cycle_name: '2025年度考核',
        employee_id: 'emp-001',
        employee_name: '王小明',
        reviewer_id: 'emp-001',
        reviewer_name: '王小明',
        review_type: 'SELF',
        evaluation_items: [
          {
            item_id: 'item-001',
            item_name: '工作品質',
            weight: 0.30,
            score: 4,
            max_score: 5,
          },
          {
            item_id: 'item-002',
            item_name: '專業能力',
            weight: 0.25,
            score: 4,
            max_score: 5,
          },
        ],
        status: 'DRAFT',
        created_at: '2025-12-01T09:00:00Z',
        updated_at: '2025-12-05T10:30:00Z',
      };

      const viewModel = PerformanceViewModelFactory.createReviewViewModel(dto);

      expect(viewModel.reviewId).toBe('review-001');
      expect(viewModel.reviewTypeLabel).toBe('自評');
      expect(viewModel.statusLabel).toBe('草稿');
      expect(viewModel.statusColor).toBe('default');
      expect(viewModel.isDraft).toBe(true);
      expect(viewModel.canEdit).toBe(true);
      expect(viewModel.canSubmit).toBe(true);
      expect(viewModel.evaluationItems).toHaveLength(2);
    });

    it('should transform submitted review with overall score', () => {
      const dto: PerformanceReviewDto = {
        review_id: 'review-002',
        cycle_id: 'cycle-001',
        cycle_name: '2025年度考核',
        employee_id: 'emp-001',
        employee_name: '王小明',
        reviewer_id: 'mgr-001',
        reviewer_name: '李主管',
        review_type: 'MANAGER',
        evaluation_items: [],
        overall_score: 4.1,
        overall_rating: 'A',
        comments: '表現優異，持續保持',
        status: 'SUBMITTED',
        submitted_at: '2025-12-15T14:00:00Z',
        created_at: '2025-12-15T09:00:00Z',
        updated_at: '2025-12-15T14:00:00Z',
      };

      const viewModel = PerformanceViewModelFactory.createReviewViewModel(dto);

      expect(viewModel.reviewTypeLabel).toBe('主管評');
      expect(viewModel.overallScoreDisplay).toBe('4.1');
      expect(viewModel.overallRatingDisplay).toBe('A (優秀)');
      expect(viewModel.overallRatingColor).toBe('success');
      expect(viewModel.statusLabel).toBe('已提交');
      expect(viewModel.isSubmitted).toBe(true);
      expect(viewModel.canEdit).toBe(false);
    });
  });

  describe('createMyPerformanceViewModel', () => {
    it('should transform my performance with current cycle', () => {
      const dto: MyPerformanceDto = {
        current_cycle: {
          cycle_id: 'cycle-001',
          cycle_name: '2025年度考核',
          cycle_type: 'ANNUAL',
          start_date: '2025-01-01',
          end_date: '2025-12-31',
          self_eval_deadline: '2026-01-15',
          status: 'IN_PROGRESS',
          created_at: '2024-12-01T00:00:00Z',
        },
        self_review: {
          review_id: 'review-001',
          cycle_id: 'cycle-001',
          cycle_name: '2025年度考核',
          employee_id: 'emp-001',
          employee_name: '王小明',
          reviewer_id: 'emp-001',
          reviewer_name: '王小明',
          review_type: 'SELF',
          evaluation_items: [],
          status: 'DRAFT',
          created_at: '2025-12-01T09:00:00Z',
          updated_at: '2025-12-05T10:30:00Z',
        },
        history: [],
        can_submit_self_eval: true,
      };

      const viewModel = PerformanceViewModelFactory.createMyPerformanceViewModel(dto);

      expect(viewModel.hasCurrentCycle).toBe(true);
      expect(viewModel.currentCycle).toBeDefined();
      expect(viewModel.selfReview).toBeDefined();
      expect(viewModel.canSubmitSelfEval).toBe(true);
      expect(viewModel.statusType).toBe('info');
      expect(viewModel.nextAction).toContain('請完成自我評估');
    });

    it('should handle no current cycle', () => {
      const dto: MyPerformanceDto = {
        history: [],
        can_submit_self_eval: false,
      };

      const viewModel = PerformanceViewModelFactory.createMyPerformanceViewModel(dto);

      expect(viewModel.hasCurrentCycle).toBe(false);
      expect(viewModel.statusMessage).toContain('目前無進行中的考核');
      expect(viewModel.statusType).toBe('warning');
    });
  });

  describe('calculateOverallScore', () => {
    it('should calculate weighted score correctly', () => {
      const items: EvaluationItemDto[] = [
        { item_id: '1', item_name: '工作品質', weight: 0.30, score: 4, max_score: 5 },
        { item_id: '2', item_name: '專業能力', weight: 0.25, score: 4, max_score: 5 },
        { item_id: '3', item_name: '團隊合作', weight: 0.20, score: 5, max_score: 5 },
        { item_id: '4', item_name: '溝通協調', weight: 0.15, score: 4, max_score: 5 },
        { item_id: '5', item_name: '創新改善', weight: 0.10, score: 3, max_score: 5 },
      ];

      const score = PerformanceViewModelFactory.calculateOverallScore(items);

      // 0.30*4 + 0.25*4 + 0.20*5 + 0.15*4 + 0.10*3 = 1.2 + 1.0 + 1.0 + 0.6 + 0.3 = 4.1
      expect(score).toBe(4.1);
    });

    it('should return 0 for empty items', () => {
      const score = PerformanceViewModelFactory.calculateOverallScore([]);
      expect(score).toBe(0);
    });
  });
});
