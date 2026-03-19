import { DollarOutlined, FieldTimeOutlined, PieChartOutlined, RiseOutlined } from '@ant-design/icons';
import { Card, Col, Empty, Progress, Row, Spin, Statistic, Table, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useEffect } from 'react';
import { useProjectCost } from '../hooks/useProjectCost';
import { MemberCostViewModel } from '../model/ProjectViewModel';

const { Title, Text } = Typography;

interface ProjectCostTabProps {
  projectId: string;
}

/**
 * 專案成本分析頁籤
 */
export const ProjectCostTab: React.FC<ProjectCostTabProps> = ({ projectId }) => {
  const { costData, loading, fetchCostData } = useProjectCost(projectId);

  useEffect(() => {
    fetchCostData();
  }, [fetchCostData]);

  if (loading && !costData) {
    return <div style={{ padding: 40, textAlign: 'center' }}><Spin /></div>;
  }

  if (!costData) {
    return <Empty description="暫無成本數據" />;
  }

  const columns: ColumnsType<MemberCostViewModel> = [
    {
      title: '成員',
      dataIndex: 'employeeName',
      key: 'employeeName',
    },
    {
      title: '工時 (h)',
      dataIndex: 'hours',
      key: 'hours',
      align: 'right',
    },
    {
      title: '平均時薪',
      dataIndex: 'hourlyRate',
      key: 'hourlyRate',
      align: 'right',
      render: (val) => `$${val.toLocaleString()}`,
    },
    {
      title: '人力成本',
      dataIndex: 'cost',
      key: 'cost',
      align: 'right',
      render: (val) => <Text strong>$${val.toLocaleString()}</Text>,
    },
    {
      title: '佔比',
      dataIndex: 'costPercentage',
      key: 'costPercentage',
      width: 150,
      render: (percent) => <Progress percent={percent} size="small" strokeColor="#722ed1" />,
    },
  ];

  return (
    <div style={{ padding: '16px 0' }}>
      <Row gutter={[16, 16]}>
        <Col span={6}>
          <Card size="small">
            <Statistic
              title="預估毛利"
              value={costData.profitMargin}
              precision={0}
              prefix={<RiseOutlined />}
              suffix="%"
              valueStyle={{ color: costData.profitMargin > 20 ? '#52c41a' : '#faad14' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card size="small">
            <Statistic
              title="總報工時"
              value={costData.actualHours}
              prefix={<FieldTimeOutlined />}
              suffix="h"
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card size="small">
            <Statistic
              title="預算使用率"
              value={costData.costUtilization}
              prefix={<DollarOutlined />}
              suffix="%"
              valueStyle={{ color: costData.costUtilization > 90 ? '#f5222d' : '#1890ff' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card size="small">
            <Statistic
              title="工時使用率"
              value={costData.hourUtilization}
              prefix={<PieChartOutlined />}
              suffix="%"
            />
          </Card>
        </Col>
      </Row>

      <div style={{ marginTop: 24 }}>
        <Title level={5}>人力成本分佈</Title>
        <Table
          columns={columns}
          dataSource={costData.memberCosts}
          rowKey="employeeId"
          pagination={false}
          size="middle"
          summary={(pageData) => {
            let totalCost = 0;
            let totalHours = 0;
            pageData.forEach(({ cost, hours }) => {
              totalCost += cost;
              totalHours += hours;
            });
            return (
              <Table.Summary.Row>
                <Table.Summary.Cell index={0}><Text strong>總計</Text></Table.Summary.Cell>
                <Table.Summary.Cell index={1} align="right"><Text strong>{totalHours}h</Text></Table.Summary.Cell>
                <Table.Summary.Cell index={2}></Table.Summary.Cell>
                <Table.Summary.Cell index={3} align="right">
                  <Text strong type="danger">$${totalCost.toLocaleString()}</Text>
                </Table.Summary.Cell>
                <Table.Summary.Cell index={4}></Table.Summary.Cell>
              </Table.Summary.Row>
            );
          }}
        />
      </div>
    </div>
  );
};
