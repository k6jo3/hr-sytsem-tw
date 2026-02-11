import { SyncOutlined } from '@ant-design/icons';
import { Button, Card, message, Select, Space, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import type { ReviewStatus } from '../features/performance/api/PerformanceTypes';
import { useCycles } from '../features/performance/hooks/useCycles';
import { useTeamReviews } from '../features/performance/hooks/useTeamReviews';
import type { TeamReviewItemViewModel } from '../features/performance/model/PerformanceViewModel';

const { Option } = Select;
const { Title } = Typography;

/**
 * HR08-P04 團隊考核頁面
 */
export const HR08TeamPerformancePage: React.FC = () => {
  const { reviews, loading, fetchReviews } = useTeamReviews();
  const { cycles, fetchCycles } = useCycles();
  
  const [selectedCycleId, setSelectedCycleId] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<ReviewStatus | 'ALL'>('ALL');

  useEffect(() => {
    fetchCycles({ status: 'IN_PROGRESS' });
  }, [fetchCycles]);

  useEffect(() => {
    if (cycles.length > 0 && !selectedCycleId) {
      setSelectedCycleId(cycles[0]?.cycleId || '');
    }
  }, [cycles, selectedCycleId]);

  useEffect(() => {
    if (selectedCycleId) {
      fetchReviews({
        cycle_id: selectedCycleId,
        status: statusFilter === 'ALL' ? undefined : statusFilter,
      });
    }
  }, [selectedCycleId, statusFilter, fetchReviews]);

  const handleReview = (record: TeamReviewItemViewModel) => {
    message.info('開啟主管評核視窗 (待實作)');
    // Open ManagerReviewModal here
  };

  const columns = [
    {
      title: '員工姓名',
      dataIndex: 'employeeName',
      key: 'employeeName',
      render: (text: string, record: TeamReviewItemViewModel) => (
        <span>{text} <small style={{ color: '#999' }}>({record.employeeCode})</small></span>
      ),
    },
    {
      title: '部門',
      dataIndex: 'departmentName',
      key: 'department',
    },
    {
      title: '職稱',
      dataIndex: 'positionName',
      key: 'position',
    },
    {
      title: '自評狀態',
      dataIndex: 'selfReviewStatusLabel',
      key: 'selfStatus',
      render: (text: string, record: TeamReviewItemViewModel) => (
        <Tag color={record.selfReviewStatusColor}>{text}</Tag>
      ),
    },
    {
      title: '主管評狀態',
      dataIndex: 'managerReviewStatusLabel',
      key: 'managerStatus',
      render: (text: string, record: TeamReviewItemViewModel) => (
        <Tag color={record.managerReviewStatusColor}>{text}</Tag>
      ),
    },
    {
      title: '總分',
      dataIndex: 'overallScoreDisplay',
      key: 'score',
      align: 'right' as const,
    },
    {
      title: '評等',
      dataIndex: 'overallRating',
      key: 'rating',
      align: 'center' as const,
      render: (text: string, record: TeamReviewItemViewModel) => (
        text ? <Tag color={record.overallRatingColor}>{text}</Tag> : '-'
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: TeamReviewItemViewModel) => (
        <Space size="small">
          <Button 
            type="primary" 
            size="small"
            disabled={!record.needsManagerReview && record.managerReviewStatusLabel === '已定案'}
            onClick={() => handleReview(record)}
          >
            {record.managerReviewStatusLabel === '已定案' ? '查看' : '評核'}
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Card>
        <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>團隊考核管理</Title>
          <Space>
             <Select 
               style={{ width: 200 }} 
               placeholder="選擇考核週期"
               value={selectedCycleId}
               onChange={setSelectedCycleId}
             >
               {cycles.map(cycle => (
                 <Option key={cycle.cycleId} value={cycle.cycleId}>{cycle.cycleName}</Option>
               ))}
             </Select>
             <Select 
               style={{ width: 120 }} 
               value={statusFilter}
               onChange={setStatusFilter}
             >
               <Option value="ALL">全部狀態</Option>
               <Option value="SUBMITTED">待評核</Option>
               <Option value="DRAFT">評核中</Option>
               <Option value="FINALIZED">已完成</Option>
             </Select>
             <Button icon={<SyncOutlined />} onClick={() => {
               if (selectedCycleId) fetchReviews({ cycle_id: selectedCycleId, status: statusFilter === 'ALL' ? undefined : statusFilter });
             }} />
          </Space>
        </div>

        <Table
          columns={columns}
          dataSource={reviews}
          rowKey="employeeId"
          loading={loading}
          pagination={{ pageSize: 10 }}
        />
      </Card>
    </div>
  );
};
