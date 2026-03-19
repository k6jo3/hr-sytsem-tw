import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Popconfirm, Progress, Table, Tag, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useEffect, useState } from 'react';
import { AddProjectMemberRequest } from '../api/ProjectTypes';
import { useProjectMembers } from '../hooks/useProjectMembers';
import { ProjectMemberViewModel } from '../model/ProjectViewModel';
import { ProjectMemberModal } from './ProjectMemberModal';

const { Text } = Typography;

interface ProjectMembersTabProps {
  projectId: string;
}

/**
 * 專案成員頁籤元件
 */
export const ProjectMembersTab: React.FC<ProjectMembersTabProps> = ({ projectId }) => {
  const { members, loading, fetchMembers, addMember, removeMember } = useProjectMembers(projectId);
  const [modalVisible, setModalVisible] = useState(false);

  useEffect(() => {
    fetchMembers();
  }, [fetchMembers]);

  const handleAddMember = async (values: AddProjectMemberRequest) => {
    await addMember(values);
    setModalVisible(false);
  };

  const columns: ColumnsType<ProjectMemberViewModel> = [
    {
      title: '成員姓名',
      dataIndex: 'employeeName',
      key: 'employeeName',
      render: (text) => <Text strong>{text}</Text>,
    },
    {
      title: '專案角色',
      dataIndex: 'role',
      key: 'role',
      render: (role) => <Tag color="blue">{role}</Tag>,
    },
    {
      title: '分配工時',
      dataIndex: 'allocatedHours',
      key: 'allocatedHours',
      render: (hours) => `${hours}h`,
    },
    {
      title: '累計工時',
      dataIndex: 'actualHours',
      key: 'actualHours',
      render: (hours) => `${hours}h`,
    },
    {
      title: '投入進度',
      key: 'utilization',
      width: 200,
      render: (_, record) => (
        <Progress 
          percent={record.utilization} 
          size="small" 
          status={record.utilization > 100 ? 'exception' : 'active'} 
        />
      ),
    },
    {
      title: '加入日期',
      dataIndex: 'joinDate',
      key: 'joinDate',
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Popconfirm
          title="確定要移除此成員嗎？"
          onConfirm={() => removeMember(record.employeeId)}
          okText="確定"
          cancelText="取消"
        >
          <Button type="text" danger icon={<DeleteOutlined />} />
        </Popconfirm>
      ),
    },
  ];

  return (
    <div style={{ padding: '16px 0' }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'flex-end' }}>
        <Button 
          type="primary" 
          icon={<PlusOutlined />}
          onClick={() => setModalVisible(true)}
        >
          新增成員
        </Button>
      </div>
      <Table
        columns={columns}
        dataSource={members}
        rowKey="employeeId"
        loading={loading}
        scroll={{ x: 'max-content' }}
        pagination={false}
      />
      
      <ProjectMemberModal
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onSubmit={handleAddMember}
        loading={loading}
      />
    </div>
  );
};

// 為了讓上面的 message.info 正常運作，需要 import

