/**
 * ReportChart 元件測試
 * Domain Code: HR14
 */

import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ReportChart } from './ReportChart';

// Mock echarts-for-react
vi.mock('echarts-for-react', () => ({
  default: (props: { option: unknown; style: { height: number } }) => (
    <div data-testid="echarts-mock" style={props.style}>
      ECharts Mock
    </div>
  ),
}));

const mockData = [
  { name: '研發部', value: 30 },
  { name: '業務部', value: 25 },
  { name: '人資部', value: 15 },
];

describe('ReportChart', () => {
  it('應渲染 bar 圖表容器', () => {
    render(<ReportChart type="bar" data={mockData} title="部門人數" />);

    expect(screen.getByTestId('echarts-mock')).toBeInTheDocument();
  });

  it('應渲染 line 圖表容器', () => {
    render(<ReportChart type="line" data={mockData} />);

    expect(screen.getByTestId('echarts-mock')).toBeInTheDocument();
  });

  it('應渲染 pie 圖表容器', () => {
    render(<ReportChart type="pie" data={mockData} />);

    expect(screen.getByTestId('echarts-mock')).toBeInTheDocument();
  });

  it('空資料時應顯示 Empty', () => {
    render(<ReportChart type="bar" data={[]} />);

    expect(screen.getByText('無資料')).toBeInTheDocument();
    expect(screen.queryByTestId('echarts-mock')).not.toBeInTheDocument();
  });

  it('應支援自訂高度', () => {
    render(<ReportChart type="bar" data={mockData} height={500} />);

    const chart = screen.getByTestId('echarts-mock');
    expect(chart.style.height).toBe('500px');
  });

  it('預設高度為 300', () => {
    render(<ReportChart type="bar" data={mockData} />);

    const chart = screen.getByTestId('echarts-mock');
    expect(chart.style.height).toBe('300px');
  });
});
