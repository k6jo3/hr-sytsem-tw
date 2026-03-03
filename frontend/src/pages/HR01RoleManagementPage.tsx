/**
 * HR01 角色權限管理頁面
 * 頁面代碼：HR01-P03
 * 路由：/admin/roles
 */

import React, { useState, useCallback, useEffect } from 'react';
import {
  Layout,
  Card,
  Row,
  Col,
  Button,
  Typography,
  Space,
  Divider,
  Empty,
  Spin,
  message,
  Modal,
  Switch,
} from 'antd';
import { PlusOutlined, SaveOutlined, DeleteOutlined } from '@ant-design/icons';
import { RoleCard } from '@features/auth/components/RoleCard';
import { RoleFormModal } from '@features/auth/components/RoleFormModal';
import { PermissionTree } from '@features/auth/components/PermissionTree';
import { useRoles } from '@features/auth/hooks/useRoles';
import type { RoleDto, CreateRoleRequest, UpdateRoleRequest } from '@features/auth/api/AuthTypes';

const { Content } = Layout;
const { Title, Text, Paragraph } = Typography;

/**
 * 角色權限管理頁面
 */
const HR01RoleManagementPage: React.FC = () => {
  // 狀態
  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingRole, setEditingRole] = useState<RoleDto | null>(null);
  const [modalLoading, setModalLoading] = useState(false);
  const [selectedPermissions, setSelectedPermissions] = useState<string[]>([]);
  const [permissionsDirty, setPermissionsDirty] = useState(false);
  const [savingPermissions, setSavingPermissions] = useState(false);

  // 使用 useRoles hook
  const {
    roles,
    permissions,
    selectedRole,
    loading,
    error,
    selectRole,
    createRole,
    updateRole,
    updateRolePermissions,
    deleteRole,
    toggleRoleStatus,
  } = useRoles();

  // 顯示錯誤訊息
  useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  // 當選中角色變更時，更新選中的權限
  useEffect(() => {
    if (selectedRole) {
      setSelectedPermissions(selectedRole.permission_ids);
      setPermissionsDirty(false);
    }
  }, [selectedRole]);

  // 處理新增角色
  const handleAddRole = useCallback(() => {
    setEditingRole(null);
    setFormModalOpen(true);
  }, []);

  // 處理編輯角色
  const handleEditRole = useCallback((role: RoleDto) => {
    setEditingRole(role);
    setFormModalOpen(true);
  }, []);

  // 處理選擇角色
  const handleSelectRole = useCallback((role: RoleDto) => {
    if (permissionsDirty) {
      Modal.confirm({
        title: '未儲存的變更',
        content: '權限設定已變更但尚未儲存，確定要切換角色嗎？',
        okText: '確定',
        cancelText: '取消',
        onOk: () => {
          selectRole(role);
        },
      });
    } else {
      selectRole(role);
    }
  }, [permissionsDirty, selectRole]);

  // 處理表單提交
  const handleFormSubmit = useCallback(async (values: CreateRoleRequest | UpdateRoleRequest) => {
    setModalLoading(true);
    try {
      if (editingRole) {
        await updateRole(editingRole.id, values as UpdateRoleRequest);
        message.success('角色更新成功');
      } else {
        await createRole(values as CreateRoleRequest);
        message.success('角色建立成功');
      }
      setFormModalOpen(false);
      setEditingRole(null);
    } catch (err) {
      message.error(err instanceof Error ? err.message : '操作失敗');
    } finally {
      setModalLoading(false);
    }
  }, [editingRole, createRole, updateRole]);

  // 處理權限變更
  const handlePermissionChange = useCallback((checkedKeys: string[]) => {
    setSelectedPermissions(checkedKeys);
    setPermissionsDirty(true);
  }, []);

  // 儲存權限設定
  const handleSavePermissions = useCallback(async () => {
    if (!selectedRole) return;

    setSavingPermissions(true);
    try {
      await updateRolePermissions(selectedRole.id, selectedPermissions);
      message.success('權限設定已儲存');
      setPermissionsDirty(false);
    } catch (err) {
      message.error(err instanceof Error ? err.message : '儲存失敗');
    } finally {
      setSavingPermissions(false);
    }
  }, [selectedRole, selectedPermissions, updateRolePermissions]);

  // 取消權限變更
  const handleCancelPermissions = useCallback(() => {
    if (selectedRole) {
      setSelectedPermissions(selectedRole.permission_ids);
      setPermissionsDirty(false);
    }
  }, [selectedRole]);

  // 處理刪除角色
  const handleDeleteRole = useCallback(async () => {
    if (!selectedRole) return;

    if (selectedRole.is_system) {
      message.warning('系統角色無法刪除');
      return;
    }

    if (selectedRole.user_count > 0) {
      message.warning('此角色仍有使用者，無法刪除');
      return;
    }

    Modal.confirm({
      title: '確認刪除',
      content: `確定要刪除角色「${selectedRole.role_name}」嗎？此操作無法復原。`,
      okText: '刪除',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteRole(selectedRole.id);
          message.success('角色已刪除');
        } catch (err) {
          message.error(err instanceof Error ? err.message : '刪除失敗');
        }
      },
    });
  }, [selectedRole, deleteRole]);

  // 處理角色狀態切換
  const handleToggleStatus = useCallback(async (checked: boolean) => {
    if (!selectedRole) return;

    if (selectedRole.is_system) {
      message.warning('系統角色狀態無法變更');
      return;
    }

    try {
      await toggleRoleStatus(selectedRole.id, checked);
      message.success(`角色已${checked ? '啟用' : '停用'}`);
    } catch (err) {
      message.error(err instanceof Error ? err.message : '操作失敗');
    }
  }, [selectedRole, toggleRoleStatus]);

  return (
    <Layout style={{ minHeight: '100vh', background: '#f0f2f5' }}>
      <Content style={{ padding: 24 }}>
        <Row gutter={16}>
          {/* 左側：角色列表 */}
          <Col xs={24} sm={24} md={8} lg={8}>
            <Card
              title="角色列表"
              extra={
                <Button
                  type="primary"
                  size="small"
                  icon={<PlusOutlined />}
                  onClick={handleAddRole}
                >
                  新增
                </Button>
              }
              style={{ height: 'calc(100vh - 112px)' }}
              bodyStyle={{ overflow: 'auto', maxHeight: 'calc(100vh - 180px)' }}
            >
              {loading ? (
                <div style={{ textAlign: 'center', padding: 40 }}>
                  <Spin />
                </div>
              ) : roles.length === 0 ? (
                <Empty description="暫無角色資料" />
              ) : (
                roles.map((role) => (
                  <RoleCard
                    key={role.id}
                    role={role}
                    selected={selectedRole?.id === role.id}
                    onClick={handleSelectRole}
                    onEdit={handleEditRole}
                  />
                ))
              )}
            </Card>
          </Col>

          {/* 右側：角色詳細資訊與權限設定 */}
          <Col xs={24} sm={24} md={16} lg={16}>
            <Card style={{ height: 'calc(100vh - 112px)' }}>
              {selectedRole ? (
                <>
                  {/* 角色資訊區 */}
                  <div style={{ marginBottom: 16 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                      <div>
                        <Title level={4} style={{ marginBottom: 4 }}>
                          {selectedRole.role_name}
                        </Title>
                        <Space>
                          <Text type="secondary">代碼：{selectedRole.role_code}</Text>
                          {selectedRole.is_system && (
                            <Text type="secondary">（系統角色）</Text>
                          )}
                        </Space>
                        {selectedRole.description && (
                          <Paragraph type="secondary" style={{ marginTop: 8, marginBottom: 0 }}>
                            {selectedRole.description}
                          </Paragraph>
                        )}
                      </div>
                      <Space>
                        <Text>狀態：</Text>
                        <Switch
                          checked={selectedRole.is_active}
                          disabled={selectedRole.is_system}
                          onChange={handleToggleStatus}
                        />
                        <Button
                          danger
                          icon={<DeleteOutlined />}
                          disabled={selectedRole.is_system || selectedRole.user_count > 0}
                          onClick={handleDeleteRole}
                        >
                          刪除角色
                        </Button>
                      </Space>
                    </div>
                  </div>

                  <Divider />

                  {/* 權限設定區 */}
                  <div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                      <Title level={5} style={{ marginBottom: 0 }}>權限設定</Title>
                      {permissionsDirty && (
                        <Space>
                          <Button onClick={handleCancelPermissions}>
                            取消
                          </Button>
                          <Button
                            type="primary"
                            icon={<SaveOutlined />}
                            loading={savingPermissions}
                            onClick={handleSavePermissions}
                          >
                            儲存變更
                          </Button>
                        </Space>
                      )}
                    </div>

                    <div style={{ maxHeight: 'calc(100vh - 380px)', overflow: 'auto' }}>
                      <PermissionTree
                        permissions={permissions}
                        checkedKeys={selectedPermissions}
                        disabled={selectedRole.is_system}
                        onCheck={handlePermissionChange}
                      />
                    </div>
                  </div>
                </>
              ) : (
                <div style={{ textAlign: 'center', padding: 80 }}>
                  <Empty description="請從左側選擇一個角色以檢視詳細資訊" />
                </div>
              )}
            </Card>
          </Col>
        </Row>

        {/* 角色表單 Modal */}
        <RoleFormModal
          open={formModalOpen}
          loading={modalLoading}
          role={editingRole}
          onSubmit={handleFormSubmit}
          onCancel={() => {
            setFormModalOpen(false);
            setEditingRole(null);
          }}
        />
      </Content>
    </Layout>
  );
};

export default HR01RoleManagementPage;
