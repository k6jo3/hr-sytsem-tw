import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { SelfEvaluationForm } from './SelfEvaluationForm';
import type { EvaluationItemViewModel } from '../model/PerformanceViewModel';

const mockItems: EvaluationItemViewModel[] = [
  {
    itemId: 'item-001',
    itemName: '工作品質',
    weight: 0.3,
    weightDisplay: '30%',
    score: 4,
    scoreDisplay: '4',
    comments: '表現良好',
    maxScore: 5,
    isRequired: true,
    weightedScore: 1.2,
    weightedScoreDisplay: '1.2',
  },
  {
    itemId: 'item-002',
    itemName: '團隊合作',
    weight: 0.2,
    weightDisplay: '20%',
    score: 3,
    scoreDisplay: '3',
    maxScore: 5,
    isRequired: true,
    weightedScore: 0.6,
    weightedScoreDisplay: '0.6',
  },
];

const mockItemsNoScore: EvaluationItemViewModel[] = [
  {
    itemId: 'item-001',
    itemName: '工作品質',
    weight: 0.3,
    weightDisplay: '30%',
    scoreDisplay: '-',
    maxScore: 5,
    isRequired: true,
  },
];

describe('SelfEvaluationForm', () => {
  const defaultProps = {
    evaluationItems: mockItems,
    onSave: vi.fn().mockResolvedValue(undefined),
    onSubmit: vi.fn().mockResolvedValue(undefined),
    canEdit: true,
    canSubmit: true,
  };

  describe('渲染', () => {
    it('應正確顯示評估項目', () => {
      render(<SelfEvaluationForm {...defaultProps} />);

      expect(screen.getByText('自我評估')).toBeInTheDocument();
      expect(screen.getByText('工作品質')).toBeInTheDocument();
      expect(screen.getByText('團隊合作')).toBeInTheDocument();
    });

    it('應顯示加權總分', () => {
      render(<SelfEvaluationForm {...defaultProps} />);

      expect(screen.getByText('加權總分')).toBeInTheDocument();
    });

    it('可編輯時應顯示儲存與送出按鈕', () => {
      render(<SelfEvaluationForm {...defaultProps} />);

      expect(screen.getByText('儲存草稿')).toBeInTheDocument();
      expect(screen.getByText('送出自評')).toBeInTheDocument();
    });
  });

  describe('不可編輯模式', () => {
    it('不可編輯時不應顯示操作按鈕', () => {
      render(<SelfEvaluationForm {...defaultProps} canEdit={false} />);

      expect(screen.queryByText('儲存草稿')).not.toBeInTheDocument();
      expect(screen.queryByText('送出自評')).not.toBeInTheDocument();
    });
  });

  describe('驗證', () => {
    it('未評分時應顯示警告', () => {
      render(<SelfEvaluationForm {...defaultProps} evaluationItems={mockItemsNoScore} />);

      expect(screen.getByText('請為所有評估項目評分')).toBeInTheDocument();
    });
  });

  describe('操作', () => {
    it('點擊儲存草稿應呼叫 onSave', async () => {
      const onSave = vi.fn().mockResolvedValue(undefined);
      render(<SelfEvaluationForm {...defaultProps} onSave={onSave} />);

      fireEvent.click(screen.getByText('儲存草稿'));

      // onSave is called asynchronously
      expect(onSave).toHaveBeenCalled();
    });
  });
});
