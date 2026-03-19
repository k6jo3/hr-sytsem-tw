import React from 'react';
import { Card, Typography, Spin, Alert, Row, Col, Tag, Space, Empty, Button } from 'antd';
import { MailOutlined, PhoneOutlined, UserOutlined, ReloadOutlined } from '@ant-design/icons';
import { PageHeader } from '@shared/components/PageHeader';
import { useCandidatesKanban } from '../features/recruitment/hooks/useCandidatesKanban';
import type {
  CandidateViewModel,
  KanbanColumnViewModel,
} from '../features/recruitment/model/RecruitmentViewModel';

const { Text } = Typography;

const CandidateCard: React.FC<{ candidate: CandidateViewModel }> = ({ candidate }) => {
  return (
    <Card
      size="small"
      style={{
        marginBottom: 12,
        cursor: 'pointer',
        borderLeft: `4px solid var(--ant-color-${candidate.statusColor})`,
      }}
      hoverable
    >
      <Space direction="vertical" size="small" style={{ width: '100%' }}>
        <Text strong style={{ fontSize: 14 }}>
          {candidate.fullName}
        </Text>
        <Text type="secondary" style={{ fontSize: 12 }}>
          {candidate.jobTitle}
        </Text>
        <Space size="small" wrap>
          <Tag color={candidate.sourceColor} style={{ fontSize: 11 }}>
            {candidate.sourceLabel}
          </Tag>
          {candidate.referrerName && (
            <Tag icon={<UserOutlined />} style={{ fontSize: 11 }}>
              {candidate.referrerName}
            </Tag>
          )}
        </Space>
        <Space direction="vertical" size={0} style={{ fontSize: 11 }}>
          <Text type="secondary">
            <MailOutlined /> {candidate.email}
          </Text>
          {candidate.phoneNumber && (
            <Text type="secondary">
              <PhoneOutlined /> {candidate.phoneNumber}
            </Text>
          )}
        </Space>
        <Text type="secondary" style={{ fontSize: 11 }}>
          {candidate.daysAgoDisplay}
        </Text>
      </Space>
    </Card>
  );
};

const KanbanColumn: React.FC<{ column: KanbanColumnViewModel }> = ({ column }) => {
  return (
    <Card
      title={
        <Space>
          <span>{column.title}</span>
          <Tag color={column.color}>{column.count}</Tag>
        </Space>
      }
      style={{ height: '100%', minHeight: 500 }}
      styles={{ body: { padding: 12, overflowY: 'auto', maxHeight: 600 } }}
    >
      {column.candidates.length === 0 ? (
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description="無資料"
          style={{ margin: '40px 0' }}
        />
      ) : (
        column.candidates.map((candidate) => (
          <CandidateCard key={candidate.candidateId} candidate={candidate} />
        ))
      )}
    </Card>
  );
};

export const HR09RecruitmentPage: React.FC = () => {
  const { kanban, loading, error, refresh } = useCandidatesKanban();

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return (
      <Card style={{ margin: 24 }}>
        <Alert message="載入失敗" description={error} type="error" showIcon />
      </Card>
    );
  }

  if (!kanban) {
    return (
      <Card style={{ margin: 24 }}>
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="暫無招募資料" />
      </Card>
    );
  }

  return (
    <div style={{ padding: 24 }}>
      <PageHeader
        title="招募管理"
        subtitle="拖曳卡片以更新應徵者狀態"
        breadcrumbs={[
          { title: '人才招募' },
          { title: '招募管理' },
        ]}
        extra={
          <Button icon={<ReloadOutlined />} onClick={refresh}>
            重新整理
          </Button>
        }
      />

      <Row gutter={16} style={{ marginBottom: 24 }}>
        {kanban.columns.map((column) => (
          <Col xs={12} sm={8} md={4} key={column.id}>
            <Card size="small" style={{ textAlign: 'center' }}>
              <div
                style={{
                  fontSize: 24,
                  fontWeight: 'bold',
                  color: `var(--ant-color-${column.color})`,
                }}
              >
                {column.count}
              </div>
              <Text type="secondary" style={{ whiteSpace: 'nowrap' }}>{column.title}</Text>
            </Card>
          </Col>
        ))}
        <Col span={4}>
          <Card size="small" style={{ textAlign: 'center' }}>
            <div style={{ fontSize: 24, fontWeight: 'bold' }}>{kanban.candidates.length}</div>
            <Text type="secondary">總應徵人數</Text>
          </Card>
        </Col>
      </Row>

      <Row gutter={16}>
        {kanban.columns.map((column) => (
          <Col xs={24} sm={12} md={8} lg={4} key={column.id}>
            <KanbanColumn column={column} />
          </Col>
        ))}
      </Row>
    </div>
  );
};
