import React from 'react';
import { Card, message, Input, Select, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useProjects } from '../features/project/hooks/useProjects';
import { ProjectList } from '../features/project/components/ProjectList';
import type { ProjectViewModel } from '../features/project/model/ProjectViewModel';
import type { ProjectStatus, ProjectType } from '../features/project/api/ProjectTypes';

const { Search } = Input;
const { Option } = Select;

/**
 * HR06-P02: 專案列表頁面
 */
export const HR06ProjectListPage: React.FC = () => {
  const navigate = useNavigate();
  const {
    projects,
    loading,
    error,
    total,
    page,
    pageSize,
    filters,
    handlePageChange,
    handleFilterChange,
    refresh,
  } = useProjects();

  // 顯示錯誤訊息
  React.useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  const handleSearch = (keyword: string) => {
    handleFilterChange({ ...filters, keyword });
  };

  const handleStatusChange = (status?: ProjectStatus) => {
    handleFilterChange({ ...filters, status });
  };

  const handleTypeChange = (projectType?: ProjectType) => {
    handleFilterChange({ ...filters, projectType });
  };

  const handleAddProject = () => {
    navigate('/admin/projects/new');
  };

  const handleRowClick = (project: ProjectViewModel) => {
    navigate(`/admin/projects/${project.id}`);
  };

  return (
    <div style={{ padding: 24 }}>
      <Card
        title={<span style={{ fontSize: 20, fontWeight: 600 }}>專案管理</span>}
        extra={
          <Space>
            <Select
              placeholder="狀態"
              allowClear
              style={{ width: 120 }}
              onChange={handleStatusChange}
              value={filters.status}
            >
              <Option value="PLANNING">規劃中</Option>
              <Option value="IN_PROGRESS">進行中</Option>
              <Option value="COMPLETED">已結案</Option>
              <Option value="ON_HOLD">暫停</Option>
              <Option value="CANCELLED">已取消</Option>
            </Select>
            <Select
              placeholder="類型"
              allowClear
              style={{ width: 120 }}
              onChange={handleTypeChange}
              value={filters.projectType}
            >
              <Option value="DEVELOPMENT">新開發</Option>
              <Option value="MAINTENANCE">維護</Option>
              <Option value="CONSULTING">顧問</Option>
            </Select>
            <Search
              placeholder="搜尋專案..."
              allowClear
              onSearch={handleSearch}
              style={{ width: 250 }}
            />
          </Space>
        }
      >
        <ProjectList
          projects={projects}
          loading={loading}
          total={total}
          page={page}
          pageSize={pageSize}
          onPageChange={handlePageChange}
          onAdd={handleAddProject}
          onRefresh={refresh}
          onRowClick={handleRowClick}
        />
      </Card>
    </div>
  );
};
