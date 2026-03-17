import { PlusOutlined, TeamOutlined } from '@ant-design/icons';
import { PageHeader } from '@shared/components/PageHeader';
import { Button, Card, Col, Input, Radio, Row, Space } from 'antd';
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ProjectList } from '../features/project/components/ProjectList';
import { useProjects } from '../features/project/hooks/useProjects';

/**
 * HR06-P02: 專案列表頁面
 */
export const HR06ProjectListPage: React.FC = () => {
  const navigate = useNavigate();
  const { 
    projects, 
    loading, 
    total, 
    page, 
    pageSize, 
    filters, 
    handlePageChange, 
    handleFilterChange, 
    refresh 
  } = useProjects();

  const handleSearch = (value: string) => {
    handleFilterChange({ ...filters, keyword: value });
  };

  const handleStatusChange = (status: any) => {
    handleFilterChange({ ...filters, status });
  };

  const handleTypeChange = (projectType: any) => {
    handleFilterChange({ ...filters, projectType });
  };

  const handleCreate = () => {
    navigate('/admin/projects/new');
  };

  const handleManageCustomers = () => {
    navigate('/admin/projects/customers');
  };

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <PageHeader
          title="專案列表"
          subtitle="瀏覽及管理所有專案，依狀態與類型篩選"
          breadcrumbs={[
            { title: '專案管理' },
            { title: '專案列表' },
          ]}
          extra={
            <Space>
              <Button icon={<TeamOutlined />} onClick={handleManageCustomers}>客戶管理</Button>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
                新增專案
              </Button>
            </Space>
          }
        />

        <Card>
          <Space direction="vertical" size="middle" style={{ width: '100%' }}>
            <Row gutter={16} align="middle">
              <Col span={8}>
                <Input.Search
                  placeholder="搜尋專案代碼、名稱或客戶..."
                  onSearch={handleSearch}
                  allowClear
                  onChange={(e) => !e.target.value && handleSearch('')}
                />
              </Col>
              <Col span={16}>
                <Space size="large">
                  <div>
                    <span style={{ marginRight: 8 }}>狀態:</span>
                    <Radio.Group 
                      value={filters.status} 
                      onChange={(e) => handleStatusChange(e.target.value)}
                      optionType="button"
                      buttonStyle="solid"
                      size="small"
                    >
                      <Radio.Button value={undefined}>全部</Radio.Button>
                      <Radio.Button value="PLANNING">規劃中</Radio.Button>
                      <Radio.Button value="IN_PROGRESS">進行中</Radio.Button>
                      <Radio.Button value="COMPLETED">已結案</Radio.Button>
                    </Radio.Group>
                  </div>
                  <div>
                    <span style={{ marginRight: 8 }}>類型:</span>
                    <Radio.Group 
                      value={filters.projectType} 
                      onChange={(e) => handleTypeChange(e.target.value)}
                      optionType="button"
                      buttonStyle="solid"
                      size="small"
                    >
                      <Radio.Button value={undefined}>全部</Radio.Button>
                      <Radio.Button value="DEVELOPMENT">新開發</Radio.Button>
                      <Radio.Button value="MAINTENANCE">維護</Radio.Button>
                      <Radio.Button value="CONSULTING">顧問</Radio.Button>
                    </Radio.Group>
                  </div>
                </Space>
              </Col>
            </Row>

            <ProjectList
              projects={projects}
              loading={loading}
              total={total}
              page={page}
              pageSize={pageSize}
              onPageChange={handlePageChange}
              onRowClick={(project) => navigate(`/admin/projects/${project.id}`)}
              onRefresh={refresh}
            />
          </Space>
        </Card>
      </Space>
    </div>
  );
};

export default HR06ProjectListPage;
