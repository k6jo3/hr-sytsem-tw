import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import { Button, Card, Layout, Space, Table, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React from 'react';

const { Content, Header } = Layout;
const { Title } = Typography;

/**
 * 員工列表資料介面 (Placeholder)
 */
interface EmployeeRow {
  key: string;
  employeeNo: string;
  fullName: string;
  department: string;
  position: string;
  status: string;
}

/**
 * HR02 員工列表頁面
 * 頁面代碼：HR02-P01
 */
const HR02EmployeeListPage: React.FC = () => {
  // Placeholder 資料
  const dataSource: EmployeeRow[] = [
    {
      key: '1',
      employeeNo: 'EMP001',
      fullName: '王小明',
      department: '人力資源部',
      position: '人資專員',
      status: '在職',
    },
    {
      key: '2',
      employeeNo: 'EMP002',
      fullName: '李小華',
      department: '研發部',
      position: '資深工程師',
      status: '在職',
    },
  ];

  const columns: ColumnsType<EmployeeRow> = [
    {
      title: '員工編號',
      dataIndex: 'employeeNo',
      key: 'employeeNo',
    },
    {
      title: '姓名',
      dataIndex: 'fullName',
      key: 'fullName',
    },
    {
      title: '部門',
      dataIndex: 'department',
      key: 'department',
    },
    {
      title: '職位',
      dataIndex: 'position',
      key: 'position',
    },
    {
      title: '狀態',
      dataIndex: 'status',
      key: 'status',
    },
    {
      title: '操作',
      key: 'actions',
      render: () => (
        <Space>
          <Button type="link" size="small">
            查看
          </Button>
          <Button type="link" size="small">
            編輯
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header
        style={{
          background: '#fff',
          padding: '0 24px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
        }}
      >
        <Title level={4} style={{ margin: 0 }}>
          員工列表
        </Title>
        <Space>
          <Button icon={<ReloadOutlined />}>重新整理</Button>
          <Button type="primary" icon={<PlusOutlined />}>
            新增員工
          </Button>
        </Space>
      </Header>
      <Content style={{ padding: 24 }}>
        <Card>
          <Table
            dataSource={dataSource}
            columns={columns}
            pagination={{ pageSize: 10, showSizeChanger: true }}
          />
        </Card>
      </Content>
    </Layout>
  );
};

export default HR02EmployeeListPage;
