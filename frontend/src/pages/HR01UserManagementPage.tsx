/**
 * HR01 使用者管理頁面
 * 頁面代碼：HR01-P02
 * 路由：/admin/users
 */

import {
    ExportOutlined,
    PlusOutlined,
    ReloadOutlined,
    SearchOutlined,
    StopOutlined,
} from '@ant-design/icons';
import type { CreateUserRequest, UpdateUserRequest } from '@features/auth/api/AuthTypes';
import { ResetPasswordModal } from '@features/auth/components/ResetPasswordModal';
import { UserFormModal } from '@features/auth/components/UserFormModal';
import { UserTable } from '@features/auth/components/UserTable';
import { useUsers } from '@features/auth/hooks/useUsers';
import type { UserListViewModel as UserViewModel } from '@features/auth/model/UserProfile';
import {
    Button,
    Card,
    Divider,
    Input,
    Layout,
    message,
    Modal,
    Pagination,
    Select,
    Space,
    Typography,
} from 'antd';
import React, { useCallback, useState } from 'react';

const { Content } = Layout;
const { Title, Text } = Typography;

/**
 * 使用者管理頁面
 */
const HR01UserManagementPage: React.FC = () => {
  // 分頁狀態
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // 篩選狀態
  const [keyword, setKeyword] = useState('');
  const [statusFilter, setStatusFilter] = useState<'ACTIVE' | 'INACTIVE' | 'LOCKED' | undefined>();
  const [roleFilter, setRoleFilter] = useState<string | undefined>();

  // 批次選擇狀態
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);

  // Modal 狀態
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<UserViewModel | null>(null);
  const [resetPasswordModalOpen, setResetPasswordModalOpen] = useState(false);
  const [resetPasswordUserId, setResetPasswordUserId] = useState<string | null>(null);
  const [modalLoading, setModalLoading] = useState(false);

  // 使用 useUsers hook
  const {
    users,
    roles,
    total,
    loading,
    error,
    refresh,
    createUser,
    updateUser,
    deactivateUser,
    activateUser,
    unlockUser,
    deleteUser,
    resetPassword,
    batchDeactivate,
  } = useUsers({
    page,
    page_size: pageSize,
    keyword: keyword || undefined,
    status: statusFilter,
    role_id: roleFilter,
  });

  // 顯示錯誤訊息
  React.useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  // 處理搜尋
  const handleSearch = useCallback((value: string) => {
    setKeyword(value);
    setPage(1);
  }, []);

  // 處理篩選變更
  const handleStatusChange = useCallback((value: string | undefined) => {
    setStatusFilter(value as 'ACTIVE' | 'INACTIVE' | 'LOCKED' | undefined);
    setPage(1);
  }, []);

  const handleRoleChange = useCallback((value: string | undefined) => {
    setRoleFilter(value);
    setPage(1);
  }, []);

  // 處理分頁變更
  const handlePageChange = useCallback((newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  }, []);

  // 處理新增使用者
  const handleAdd = useCallback(() => {
    setEditingUser(null);
    setFormModalOpen(true);
  }, []);

  // 處理編輯使用者
  const handleEdit = useCallback((user: UserViewModel) => {
    setEditingUser(user);
    setFormModalOpen(true);
  }, []);

  // 處理表單提交
  const handleFormSubmit = useCallback(async (values: CreateUserRequest | UpdateUserRequest) => {
    setModalLoading(true);
    try {
      if (editingUser) {
        await updateUser(editingUser.id, values as UpdateUserRequest);
        message.success('使用者更新成功');
      } else {
        await createUser(values as CreateUserRequest);
        message.success('使用者建立成功');
      }
      setFormModalOpen(false);
      setEditingUser(null);
    } catch (err) {
      message.error(err instanceof Error ? err.message : '操作失敗');
    } finally {
      setModalLoading(false);
    }
  }, [editingUser, createUser, updateUser]);

  // 處理停用使用者
  const handleDeactivate = useCallback(async (userId: string) => {
    Modal.confirm({
      title: '確認停用',
      content: '確定要停用此使用者嗎？',
      okText: '確定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deactivateUser(userId);
          message.success('使用者已停用');
        } catch (err) {
          message.error(err instanceof Error ? err.message : '停用失敗');
        }
      },
    });
  }, [deactivateUser]);

  // 處理啟用使用者
  const handleActivate = useCallback(async (userId: string) => {
    try {
      await activateUser(userId);
      message.success('使用者已啟用');
    } catch (err) {
      message.error(err instanceof Error ? err.message : '啟用失敗');
    }
  }, [activateUser]);

  // 處理解鎖使用者
  const handleUnlock = useCallback(async (userId: string) => {
    try {
      await unlockUser(userId);
      message.success('使用者已解鎖');
    } catch (err) {
      message.error(err instanceof Error ? err.message : '解鎖失敗');
    }
  }, [unlockUser]);

  // 處理刪除使用者
  const handleDelete = useCallback(async (userId: string) => {
    Modal.confirm({
      title: '確認刪除',
      content: '確定要刪除此使用者嗎？此操作無法復原。',
      okText: '刪除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteUser(userId);
          message.success('使用者已刪除');
        } catch (err) {
          message.error(err instanceof Error ? err.message : '刪除失敗');
        }
      },
    });
  }, [deleteUser]);

  // 處理重置密碼
  const handleResetPasswordClick = useCallback((userId: string) => {
    setResetPasswordUserId(userId);
    setResetPasswordModalOpen(true);
  }, []);

  const handleResetPasswordSubmit = useCallback(async (
    userId: string,
    newPassword: string,
    forceChange: boolean
  ) => {
    setModalLoading(true);
    try {
      await resetPassword(userId, newPassword, forceChange);
      message.success('密碼重置成功');
      setResetPasswordModalOpen(false);
      setResetPasswordUserId(null);
    } catch (err) {
      message.error(err instanceof Error ? err.message : '重置密碼失敗');
    } finally {
      setModalLoading(false);
    }
  }, [resetPassword]);

  // 處理批次停用
  const handleBatchDeactivate = useCallback(async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('請先選擇使用者');
      return;
    }
    Modal.confirm({
      title: '確認批次停用',
      content: `確定要停用選取的 ${selectedRowKeys.length} 位使用者嗎？`,
      okText: '確定',
      cancelText: '取消',
      onOk: async () => {
        try {
          await batchDeactivate(selectedRowKeys as string[]);
          message.success(`已停用 ${selectedRowKeys.length} 位使用者`);
          setSelectedRowKeys([]);
        } catch (err) {
          message.error(err instanceof Error ? err.message : '批次停用失敗');
        }
      },
    });
  }, [selectedRowKeys, batchDeactivate]);

  // 處理批次匯出
  const handleBatchExport = useCallback(() => {
    message.info('匯出功能開發中...');
  }, []);

  return (
    <Layout style={{ minHeight: '100vh', background: '#f0f2f5' }}>
      <Content style={{ padding: 24 }}>
        <Card>
          {/* 頁面標題 */}
          <div style={{ marginBottom: 16 }}>
            <Title level={4} style={{ marginBottom: 4 }}>使用者管理</Title>
            <Text type="secondary">管理系統使用者帳號、角色分配與權限設定</Text>
          </div>

          <Divider style={{ margin: '16px 0' }} />

          {/* 工具列 */}
          <div style={{ marginBottom: 16 }}>
            <Space wrap>
              <Input.Search
                placeholder="搜尋使用者名稱、Email"
                allowClear
                style={{ width: 250 }}
                prefix={<SearchOutlined />}
                onSearch={handleSearch}
                onChange={(e) => !e.target.value && handleSearch('')}
              />
              <Select
                placeholder="狀態篩選"
                allowClear
                style={{ width: 120 }}
                onChange={handleStatusChange}
                options={[
                  { label: '全部', value: undefined },
                  { label: '啟用', value: 'ACTIVE' },
                  { label: '停用', value: 'INACTIVE' },
                  { label: '鎖定', value: 'LOCKED' },
                ]}
              />
              <Select
                placeholder="角色篩選"
                allowClear
                style={{ width: 150 }}
                onChange={handleRoleChange}
                options={[
                  { label: '全部', value: undefined },
                  ...roles.map((role) => ({
                    label: role.role_name,
                    value: role.id,
                  })),
                ]}
              />
              <Button icon={<ReloadOutlined />} onClick={refresh}>
                重新整理
              </Button>
              <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                新增使用者
              </Button>
            </Space>
          </div>

          {/* 批次操作工具列 */}
          {selectedRowKeys.length > 0 && (
            <div
              style={{
                marginBottom: 16,
                padding: '8px 16px',
                background: '#e6f7ff',
                borderRadius: 4,
              }}
            >
              <Space>
                <Text>已選擇 {selectedRowKeys.length} 個使用者</Text>
                <Button
                  size="small"
                  icon={<StopOutlined />}
                  onClick={handleBatchDeactivate}
                >
                  批次停用
                </Button>
                <Button
                  size="small"
                  icon={<ExportOutlined />}
                  onClick={handleBatchExport}
                >
                  批次匯出
                </Button>
                <Button
                  type="link"
                  size="small"
                  onClick={() => setSelectedRowKeys([])}
                >
                  取消選擇
                </Button>
              </Space>
            </div>
          )}

          {/* 使用者表格 */}
          <UserTable
            users={users}
            loading={loading}
            selectedRowKeys={selectedRowKeys}
            onSelectionChange={setSelectedRowKeys}
            onEdit={handleEdit}
            onDeactivate={handleDeactivate}
            onActivate={handleActivate}
            onUnlock={handleUnlock}
            onDelete={handleDelete}
            onResetPassword={handleResetPasswordClick}
          />

          {/* 分頁 */}
          <div style={{ marginTop: 16, textAlign: 'right' }}>
            <Pagination
              current={page}
              pageSize={pageSize}
              total={total}
              showSizeChanger
              showQuickJumper
              showTotal={(total) => `共 ${total} 筆資料`}
              onChange={handlePageChange}
              onShowSizeChange={handlePageChange}
              pageSizeOptions={['10', '20', '50', '100']}
            />
          </div>
        </Card>

        {/* 使用者表單 Modal */}
        <UserFormModal
          open={formModalOpen}
          loading={modalLoading}
          user={editingUser}
          roles={roles}
          onSubmit={handleFormSubmit}
          onCancel={() => {
            setFormModalOpen(false);
            setEditingUser(null);
          }}
        />

        {/* 重置密碼 Modal */}
        <ResetPasswordModal
          open={resetPasswordModalOpen}
          loading={modalLoading}
          userId={resetPasswordUserId}
          onSubmit={handleResetPasswordSubmit}
          onCancel={() => {
            setResetPasswordModalOpen(false);
            setResetPasswordUserId(null);
          }}
        />
      </Content>
    </Layout>
  );
};

export default HR01UserManagementPage;
