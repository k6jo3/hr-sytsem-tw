import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, Input, Space, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { CustomerList } from '../features/project/components/CustomerList';
import { CustomerModal } from '../features/project/components/CustomerModal';
import { useCustomers } from '../features/project/hooks/useCustomers';
import { CustomerViewModel } from '../features/project/model/CustomerViewModel';

const { Title } = Typography;

/**
 * HR06-P01: 客戶管理頁面
 */
export const HR06CustomerPage: React.FC = () => {
  const { customers, loading, total, fetchCustomers, createCustomer, updateCustomer, refresh } = useCustomers();
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [keyword, setKeyword] = useState('');
  
  // Modal State
  const [modalVisible, setModalVisible] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState<CustomerViewModel | null>(null);

  useEffect(() => {
    fetchCustomers({ page: page - 1, size: pageSize, keyword });
  }, [fetchCustomers, page, pageSize, keyword]);

  const handlePageChange = (p: number, ps: number) => {
    setPage(p);
    setPageSize(ps);
  };

  const handleSearch = (value: string) => {
    setKeyword(value);
    setPage(1);
  };

  const handleAdd = () => {
    setEditingCustomer(null);
    setModalVisible(true);
  };

  const handleEdit = (customer: CustomerViewModel) => {
    setEditingCustomer(customer);
    setModalVisible(true);
  };

  const handleModalSubmit = async (values: any) => {
    if (editingCustomer) {
      return await updateCustomer(editingCustomer.id, values);
    } else {
      return await createCustomer(values);
    }
  };

  const handleModalSuccess = () => {
    setModalVisible(false);
    refresh({ page: page - 1, size: pageSize, keyword });
  };

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={3} style={{ margin: 0 }}>客戶管理</Title>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增客戶
          </Button>
        </div>

        <Card>
          <div style={{ marginBottom: 16 }}>
            <Input
              placeholder="搜尋客戶名稱、代碼或統編..."
              prefix={<SearchOutlined />}
              onPressEnter={(e) => handleSearch((e.target as HTMLInputElement).value)}
              style={{ width: 300 }}
              allowClear
            />
          </div>

          <CustomerList
            customers={customers}
            loading={loading}
            total={total}
            page={page}
            pageSize={pageSize}
            onPageChange={handlePageChange}
            onEdit={handleEdit}
          />
        </Card>
      </Space>

      <CustomerModal
        visible={modalVisible}
        initialData={editingCustomer}
        onCancel={() => setModalVisible(false)}
        onSuccess={handleModalSuccess}
        onSubmit={handleModalSubmit}
        loading={loading}
      />
    </div>
  );
};

export default HR06CustomerPage;
