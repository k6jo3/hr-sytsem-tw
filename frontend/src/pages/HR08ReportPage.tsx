import { Card, Col, Progress, Row, Select, Statistic, Table, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useCycles } from '../features/performance/hooks/useCycles';
import { usePerformanceReport } from '../features/performance/hooks/usePerformanceReport';

const { Option } = Select;
const { Title } = Typography;

/**
 * HR08-P05 績效報表分析頁面
 */
export const HR08ReportPage: React.FC = () => {
  const { distribution, stats, loading, fetchDistribution } = usePerformanceReport();
  const { cycles, fetchCycles } = useCycles();
  const [selectedCycleId, setSelectedCycleId] = useState<string>('');

  useEffect(() => {
    fetchCycles();
  }, [fetchCycles]);

  useEffect(() => {
    if (cycles.length > 0 && !selectedCycleId) {
      setSelectedCycleId(cycles[0]?.cycleId || '');
    }
  }, [cycles, selectedCycleId]);

  useEffect(() => {
    if (selectedCycleId) {
      fetchDistribution({ cycle_id: selectedCycleId });
    }
  }, [selectedCycleId, fetchDistribution]);

  const columns = [
    {
      title: '績效評等',
      dataIndex: 'rating',
      key: 'rating',
      render: (text: string) => <div style={{ fontWeight: 'bold' }}>{text}</div>,
    },
    {
      title: '人數',
      dataIndex: 'count',
      key: 'count',
    },
    {
      title: '佔比',
      dataIndex: 'percentage',
      key: 'percentage',
      render: (value: number) => (
        <div style={{ width: 300 }}>
          <Progress percent={parseFloat(value.toFixed(1))} size="small" />
        </div>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Card>
        <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>績效分佈分析</Title>
          <Select 
            style={{ width: 250 }} 
            value={selectedCycleId}
            onChange={setSelectedCycleId}
            placeholder="選擇考核週期"
          >
            {cycles.map(cycle => (
              <Option key={cycle.cycleId} value={cycle.cycleId}>{cycle.cycleName}</Option>
            ))}
          </Select>
        </div>

        <Row gutter={24} style={{ marginBottom: 24 }}>
          <Col span={12}>
            <Card bordered={false} style={{ background: '#f0f2f5' }}>
              <Statistic title="總考核人數" value={stats.total} suffix="人" />
            </Card>
          </Col>
          <Col span={12}>
            <Card bordered={false} style={{ background: '#f0f2f5' }}>
              <Statistic title="平均分數" value={stats.average} precision={1} suffix="分" />
            </Card>
          </Col>
        </Row>

        <Table
          columns={columns}
          dataSource={distribution}
          rowKey="rating"
          loading={loading}
          pagination={false}
          size="middle"
        />
      </Card>
    </div>
  );
};
