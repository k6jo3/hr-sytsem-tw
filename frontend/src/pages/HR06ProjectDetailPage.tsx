import {
    ArrowLeftOutlined,
    BarsOutlined,
    DollarOutlined,
    EditOutlined,
    ProjectOutlined,
    TeamOutlined
} from '@ant-design/icons';
import {
    Alert,
    Button,
    Card,
    Col,
    Descriptions,
    Progress,
    Row,
    Space,
    Spin,
    Statistic,
    Tabs,
    Tag,
    Typography
} from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ProjectCostTab } from '../features/project/components/ProjectCostTab';
import { ProjectMembersTab } from '../features/project/components/ProjectMembersTab';
import { ProjectWbsTree } from '../features/project/components/ProjectWbsTree';
import { useProject } from '../features/project/hooks/useProject';

const { Title, Text } = Typography;

/**
 * HR06-P03: 專案詳情頁面
 */
export const HR06ProjectDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { project, tasks, loading, error, fetchProject, fetchTasks } = useProject(id);
  const [activeTab, setActiveTab] = useState('basic');

  useEffect(() => {
    if (id) {
      fetchProject(id);
      fetchTasks(id);
    }
  }, [id, fetchProject, fetchTasks]);

  if (loading && !project) {
    return (
      <div style={{ padding: '100px', textAlign: 'center' }}>
        <Spin size="large" tip="載入中..." />
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: 24 }}>
        <Alert message="錯誤" description={error} type="error" showIcon />
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/admin/projects')} style={{ marginTop: 16 }}>
          返回專案列表
        </Button>
      </div>
    );
  }

  if (!project) return null;

  const items = [
    {
      key: 'basic',
      label: (
        <span>
          <ProjectOutlined />
          基本資訊
        </span>
      ),
      children: (
        <Card size="small" bordered={false}>
          <Descriptions bordered column={2}>
            <Descriptions.Item label="客戶名稱">{project.customerName}</Descriptions.Item>
            <Descriptions.Item label="專案經理">{project.projectManagerName}</Descriptions.Item>
            <Descriptions.Item label="專案類型">
              <Tag color={project.projectTypeColor}>{project.projectTypeLabel}</Tag>
            </Descriptions.Item>
            <Descriptions.Item label="預算模式">{project.budgetTypeLabel}</Descriptions.Item>
            <Descriptions.Item label="計畫時程">{project.plannedSchedule}</Descriptions.Item>
            <Descriptions.Item label="狀態">
              <Tag color={project.statusColor}>{project.statusLabel}</Tag>
            </Descriptions.Item>
          </Descriptions>
        </Card>
      ),
    },
    {
      key: 'wbs',
      label: (
        <span>
          <BarsOutlined />
          WBS 工項
        </span>
      ),
      children: (
        <Card size="small" bordered={false}>
          <ProjectWbsTree tasks={tasks} loading={loading} />
        </Card>
      ),
    },
    {
      key: 'members',
      label: (
        <span>
          <TeamOutlined />
          專案成員
        </span>
      ),
      children: (
        <Card size="small" bordered={false}>
          <ProjectMembersTab projectId={id!} />
        </Card>
      ),
    },
    {
      key: 'cost',
      label: (
        <span>
          <DollarOutlined />
          成本分析
        </span>
      ),
      children: (
        <Card size="small" bordered={false}>
          <ProjectCostTab projectId={id!} />
        </Card>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* Header Section */}
        <Row justify="space-between" align="middle">
          <Col>
            <Space size="middle">
              <Button 
                icon={<ArrowLeftOutlined />} 
                onClick={() => navigate('/admin/projects')}
              />
              <div>
                <Text type="secondary">{project.projectCode}</Text>
                <Title level={3} style={{ margin: 0 }}>{project.projectName}</Title>
              </div>
            </Space>
          </Col>
          <Col>
            <Space>
              <Button icon={<BarsOutlined />} onClick={() => navigate(`/admin/projects/${id}/tasks`)}>管理工項</Button>
              <Button icon={<EditOutlined />} type="primary" onClick={() => navigate(`/admin/projects/edit/${id}`)}>編輯專案</Button>
            </Space>
          </Col>
        </Row>

        {/* Statistics Section */}
        <Row gutter={16}>
          <Col span={6}>
            <Card>
              <Statistic
                title="預算金額"
                value={project.budgetAmount}
                prefix="$"
                valueStyle={{ color: '#1890ff' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Statistic
                title="實際成本"
                value={project.actualCost}
                prefix="$"
                valueStyle={{ color: project.isOverBudget ? '#f5222d' : '#52c41a' }}
              />
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Text type="secondary" style={{ fontSize: '14px' }}>成本使用率</Text>
              <div style={{ marginTop: 8 }}>
                <Progress 
                  percent={project.costUtilization} 
                  status={project.isOverBudget ? 'exception' : 'active'}
                  strokeColor={project.isOverBudget ? '#f5222d' : undefined}
                />
              </div>
            </Card>
          </Col>
          <Col span={6}>
            <Card>
              <Text type="secondary" style={{ fontSize: '14px' }}>整體進度</Text>
              <div style={{ marginTop: 8 }}>
                <Progress 
                  percent={project.progress} 
                  status={project.isDelayed ? 'exception' : 'success'}
                  strokeColor={project.isDelayed ? '#faad14' : undefined}
                />
              </div>
            </Card>
          </Col>
        </Row>

        {/* Alerts Section */}
        {project.isOverBudget && (
          <Alert
            message="成本警示"
            description="當前實際成本已超過專案預算金額，請檢視相關支出與工時分佈。"
            type="error"
            showIcon
          />
        )}
        {project.isDelayed && !project.isOverBudget && (
          <Alert
            message="進度警示"
            description="成本使用率明顯高於當前進度，專案可能面臨延遲或超支風險。"
            type="warning"
            showIcon
          />
        )}

        {/* Tabs Section */}
        <Card bodyStyle={{ padding: '0 24px 24px' }}>
          <Tabs 
            activeKey={activeTab} 
            onChange={setActiveTab} 
            items={items}
          />
        </Card>
      </Space>
    </div>
  );
};

export default HR06ProjectDetailPage;
