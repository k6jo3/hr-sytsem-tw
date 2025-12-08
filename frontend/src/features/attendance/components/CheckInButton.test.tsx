import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { CheckInButton } from './CheckInButton';

describe('CheckInButton', () => {
  describe('上班打卡按鈕', () => {
    it('應該顯示上班打卡按鈕', () => {
      render(
        <CheckInButton
          type="CHECK_IN"
          disabled={false}
          loading={false}
          onClick={() => {}}
        />
      );

      expect(screen.getByText('上班打卡')).toBeInTheDocument();
    });

    it('點擊按鈕應該呼叫onClick', () => {
      const handleClick = vi.fn();

      render(
        <CheckInButton
          type="CHECK_IN"
          disabled={false}
          loading={false}
          onClick={handleClick}
        />
      );

      fireEvent.click(screen.getByText('上班打卡'));

      expect(handleClick).toHaveBeenCalledTimes(1);
    });

    it('禁用時不應該觸發onClick', () => {
      const handleClick = vi.fn();

      render(
        <CheckInButton
          type="CHECK_IN"
          disabled={true}
          loading={false}
          onClick={handleClick}
        />
      );

      const button = screen.getByText('上班打卡').closest('button');
      expect(button).toBeDisabled();

      fireEvent.click(button!);

      expect(handleClick).not.toHaveBeenCalled();
    });

    it('載入中時應該顯示載入狀態', () => {
      render(
        <CheckInButton
          type="CHECK_IN"
          disabled={false}
          loading={true}
          onClick={() => {}}
        />
      );

      const button = screen.getByText('上班打卡').closest('button');
      expect(button).toHaveClass('ant-btn-loading');
    });
  });

  describe('下班打卡按鈕', () => {
    it('應該顯示下班打卡按鈕', () => {
      render(
        <CheckInButton
          type="CHECK_OUT"
          disabled={false}
          loading={false}
          onClick={() => {}}
        />
      );

      expect(screen.getByText('下班打卡')).toBeInTheDocument();
    });

    it('點擊按鈕應該呼叫onClick', () => {
      const handleClick = vi.fn();

      render(
        <CheckInButton
          type="CHECK_OUT"
          disabled={false}
          loading={false}
          onClick={handleClick}
        />
      );

      fireEvent.click(screen.getByText('下班打卡'));

      expect(handleClick).toHaveBeenCalledTimes(1);
    });
  });

  describe('按鈕樣式', () => {
    it('上班打卡應該是primary樣式', () => {
      render(
        <CheckInButton
          type="CHECK_IN"
          disabled={false}
          loading={false}
          onClick={() => {}}
        />
      );

      const button = screen.getByText('上班打卡').closest('button');
      expect(button).toHaveClass('ant-btn-primary');
    });

    it('下班打卡應該是default樣式', () => {
      render(
        <CheckInButton
          type="CHECK_OUT"
          disabled={false}
          loading={false}
          onClick={() => {}}
        />
      );

      const button = screen.getByText('下班打卡').closest('button');
      expect(button).toHaveClass('ant-btn-default');
    });

    it('應該是大尺寸按鈕', () => {
      render(
        <CheckInButton
          type="CHECK_IN"
          disabled={false}
          loading={false}
          onClick={() => {}}
        />
      );

      const button = screen.getByText('上班打卡').closest('button');
      expect(button).toHaveClass('ant-btn-lg');
    });
  });

  describe('無障礙支援', () => {
    it('應該有正確的aria-label', () => {
      render(
        <CheckInButton
          type="CHECK_IN"
          disabled={false}
          loading={false}
          onClick={() => {}}
        />
      );

      const button = screen.getByLabelText('上班打卡');
      expect(button).toBeInTheDocument();
    });

    it('禁用時應該有aria-disabled屬性', () => {
      render(
        <CheckInButton
          type="CHECK_IN"
          disabled={true}
          loading={false}
          onClick={() => {}}
        />
      );

      const button = screen.getByLabelText('上班打卡');
      expect(button).toHaveAttribute('disabled');
    });
  });
});
