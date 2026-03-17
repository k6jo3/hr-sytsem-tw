import { PlusOutlined } from '@ant-design/icons';
import { PageHeader } from '@shared/components/PageHeader';
import { Button, Card, Input, Space } from 'antd';
import React, { useEffect, useState } from 'react';
import { CustomerList } from '../features/project/components/CustomerList';
import { CustomerModal } from '../features/project/components/CustomerModal';
import { useCustomers } from '../features/project/hooks/useCustomers';
import { CustomerViewModel } from '../features/project/model/CustomerViewModel';

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
        <PageHeader
          title="客戶管理"
          subtitle="管理專案客戶資料，包含新增、編輯客戶資訊"
          breadcrumbs={[
            { title: '專案管理', path: '/admin/projects' },
            { title: '客戶管理' },
          ]}
          extra={
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增客戶
            </Button>
          }
        />

        <Card>
          <div style={{ marginBottom: 16 }}>
            <Input.Search
              placeholder="搜尋客戶名稱、代碼或統編..."
              onSearch={handleSearch}
              allowClear
              style={{ width: 300 }}
              onChange={(e) => !e.target.value && handleSearch('')}
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
