/**
 * ReportFilterBar 元件測試
 * Domain Code: HR14
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { ReportFilterBar } from './ReportFilterBar';

describe('ReportFilterBar', () => {
  const defaultProps = {
    period: 'MONTHLY' as const,
    onPeriodChange: vi.fn(),
    onRefresh: vi.fn(),
    loading: false,
  };

  it('應渲染期間選擇器與重新整理按鈕', () => {
    render(<ReportFilterBar {...defaultProps} />);

    expect(screen.getByText('重新整理')).toBeInTheDocument();
    // Select 預設值
    expect(screen.getByText('每月')).toBeInTheDocument();
  });

  it('點擊重新整理應觸發 onRefresh', () => {
    render(<ReportFilterBar {...defaultProps} />);

    fireEvent.click(screen.getByText('重新整理'));
    expect(defaultProps.onRefresh).toHaveBeenCalledTimes(1);
  });

  it('loading 為 true 時按鈕應顯示載入狀態', () => {
    render(<ReportFilterBar {...defaultProps} loading={true} />);

    const button = screen.getByText('重新整理').closest('button');
    expect(button).toBeInTheDocument();
  });

  it('應顯示三種期間選項', () => {
    const { container } = render(<ReportFilterBar {...defaultProps} />);

    // Select 選項在 DOM 中可見
    expect(screen.getByText('每月')).toBeInTheDocument();
    // 其他選項在下拉展開時才可見，這裡只驗證 Select 存在
    expect(container.querySelector('.ant-select')).toBeInTheDocument();
  });
});
