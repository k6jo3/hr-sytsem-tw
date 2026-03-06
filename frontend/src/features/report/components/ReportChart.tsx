/**
 * ECharts 圖表包裝元件
 * Domain Code: HR14
 * 支援 bar / line / pie 三種圖表類型
 */

import React, { useMemo } from 'react';
import ReactECharts from 'echarts-for-react';
import { Empty } from 'antd';
import type { EChartsOption } from 'echarts';

export type ChartType = 'bar' | 'line' | 'pie';

interface ReportChartProps {
  type: ChartType;
  title?: string;
  data: { name: string; value: number }[];
  height?: number;
  color?: string | string[];
}

export const ReportChart: React.FC<ReportChartProps> = ({
  type,
  title,
  data,
  height = 300,
  color = '#1890ff',
}) => {
  const option: EChartsOption = useMemo(() => {
    if (data.length === 0) return {};

    const colors = Array.isArray(color) ? color : [color];

    if (type === 'pie') {
      return {
        title: title ? { text: title, left: 'center', textStyle: { fontSize: 14 } } : undefined,
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            data: data.map((d, i) => ({
              name: d.name,
              value: d.value,
              itemStyle: { color: colors[i % colors.length] },
            })),
            label: { show: true, formatter: '{b}\n{d}%' },
          },
        ],
      };
    }

    // bar / line
    return {
      title: title ? { text: title, left: 'center', textStyle: { fontSize: 14 } } : undefined,
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: data.map((d) => d.name),
        axisLabel: { rotate: data.length > 6 ? 30 : 0 },
      },
      yAxis: { type: 'value' },
      series: [
        {
          type,
          data: data.map((d) => d.value),
          itemStyle: { color: colors[0] },
          smooth: type === 'line',
        },
      ],
      grid: { left: '10%', right: '5%', bottom: '15%', top: title ? '15%' : '5%' },
    };
  }, [type, title, data, color]);

  if (data.length === 0) {
    return <Empty description="無資料" style={{ padding: '40px 0' }} />;
  }

  return (
    <ReactECharts
      option={option}
      style={{ height }}
      opts={{ renderer: 'canvas' }}
    />
  );
};
