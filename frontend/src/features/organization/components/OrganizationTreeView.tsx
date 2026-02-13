/**
 * Organization Tree View Component
 * Domain Code: HR02
 * Page Code: HR02-P01
 * Displays organization structure in a tree format
 */

import { BankOutlined, DeleteOutlined, DownOutlined, EditOutlined, PlusOutlined, ReloadOutlined, TeamOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Col, Dropdown, Empty, message, Modal, Row, Spin, Statistic, Tree } from 'antd';
import { DataNode } from 'antd/es/tree';
import React, { useEffect, useState } from 'react';
import { OrganizationApi } from '../api/OrganizationApi';
import { DepartmentDto, DepartmentRequest, OrganizationDto } from '../api/OrganizationTypes';
import { OrganizationTreeFactory } from '../factory/OrganizationTreeFactory';
import { DepartmentFormModal } from './DepartmentFormModal';

export const OrganizationTreeView: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [treeData, setTreeData] = useState<DataNode[]>([]);
    const [selectedOrg, setSelectedOrg] = useState<OrganizationDto | null>(null);
    const [error, setError] = useState<string | null>(null);

    // Modal State
    const [modalVisible, setModalVisible] = useState(false);
    const [modalLoading, setModalLoading] = useState(false);
    const [editingDept, setEditingDept] = useState<DepartmentDto | null>(null);
    const [targetParentId, setTargetParentId] = useState<string | undefined>(undefined);

    // Initial load - get the first organization or allow selection
    const loadOrganizationData = async () => {
        setLoading(true);
        setError(null);
        try {
            // 1. Get List of Organizations
            const orgsRes = await OrganizationApi.getOrganizations();
            const orgs = orgsRes.content;
            
            if (orgs.length === 0) {
                setTreeData([]);
                return;
            }

            // 2. Default to first organization (usually HEADQUARTERS)
            const targetOrgId = orgs[0]?.organizationId;
            if (!targetOrgId) return;
            
            // 3. Fetch Tree Structure
            const treeRes = await OrganizationApi.getOrganizationTree(targetOrgId);
            
            // 4. Convert to Tree Nodes using Factory
            const nodes = OrganizationTreeFactory.createTreeData(treeRes.data, treeRes.departments);
            
            setTreeData(nodes);
            setSelectedOrg(treeRes.data);

        } catch (err) {
            console.error(err);
            setError('無法載入組織架構資料，請稍後再試');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        loadOrganizationData();
    }, []);

    // Handlers
    const handleAddDepartment = (parentId?: string) => {
        setEditingDept(null);
        setTargetParentId(parentId);
        setModalVisible(true);
    };

    const handleEditDepartment = (dept: DepartmentDto) => {
        setEditingDept(dept);
        setTargetParentId(undefined); // Parent ID is inside dept
        setModalVisible(true);
    };

    const handleDeleteDepartment = (dept: DepartmentDto) => {
        Modal.confirm({
            title: `確認刪除部門「${dept.name}」？`,
            content: '刪除後無法復原，且該部門下的員工將失去部門歸屬。',
            okType: 'danger',
            onOk: async () => {
                try {
                    await OrganizationApi.deleteDepartment(dept.departmentId);
                    message.success('部門刪除成功');
                    loadOrganizationData();
                } catch (err) {
                    message.error('刪除失敗，該部門可能包含子部門或員工');
                }
            },
        });
    };

    const handleModalSubmit = async (values: DepartmentRequest) => {
        setModalLoading(true);
        try {
            if (editingDept) {
                await OrganizationApi.updateDepartment(editingDept.departmentId, values);
                message.success('部門更新成功');
            } else {
                await OrganizationApi.createDepartment(values);
                message.success('部門新增成功');
            }
            setModalVisible(false);
            loadOrganizationData();
        } catch (err) {
            console.error(err);
            message.error('儲存失敗，請稍後再試');
            throw err; // Re-throw to let modal know it failed (if modal handled it)
        } finally {
            setModalLoading(false);
        }
    };

    // Node Context Menu
    const renderNodeTitle = (node: DataNode) => {
        // Factory stores DepartmentDto in `data` property of the node
        const deptData = (node as any).data as DepartmentDto | undefined;
        // If no data, it's likely the Organization Root Node
        const isRoot = !deptData; 

        return (
            <Dropdown
                menu={{
                    items: isRoot ? [
                        {
                            key: 'add-root-dept',
                            label: '新增一級部門',
                            icon: <PlusOutlined />,
                            onClick: (e) => {
                                e.domEvent.stopPropagation();
                                handleAddDepartment();
                            }
                        }
                    ] : [
                        {
                            key: 'add-sub-dept',
                            label: '新增子部門',
                            icon: <PlusOutlined />,
                            onClick: (e) => {
                                e.domEvent.stopPropagation();
                                handleAddDepartment(deptData?.departmentId);
                            }
                        },
                        {
                            key: 'edit-dept',
                            label: '編輯部門',
                            icon: <EditOutlined />,
                            onClick: (e) => {
                                e.domEvent.stopPropagation();
                                if(deptData) handleEditDepartment(deptData);
                            }
                        },
                        {
                            key: 'delete-dept',
                            label: '刪除部門',
                            danger: true,
                            icon: <DeleteOutlined />,
                            onClick: (e) => {
                                e.domEvent.stopPropagation();
                                if(deptData) handleDeleteDepartment(deptData);
                            }
                        }
                    ]
                }}
                trigger={['contextMenu']}
            >
                <div style={{ display: 'inline-block', width: '100%' }}>
                     <span style={{ fontSize: 16, padding: '4px 0' }}>
                        {typeof node.title === 'function' ? node.title(node) : node.title} 
                    </span>
                </div>
            </Dropdown>
        );
    };

    return (
        <div style={{ padding: 24 }}>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <h2 style={{ margin: 0 }}>組織架構圖</h2>
                <div>
                     <Button 
                        type="primary"
                        icon={<PlusOutlined />} 
                        onClick={() => handleAddDepartment()}
                        style={{ marginRight: 8 }}
                        disabled={!selectedOrg}
                    >
                        新增部門
                    </Button>
                    <Button 
                        icon={<ReloadOutlined />} 
                        onClick={loadOrganizationData} 
                        loading={loading}
                    >
                        重新整理
                    </Button>
                </div>
            </div>

            {error && <Alert message={error} type="error" showIcon style={{ marginBottom: 16 }} />}

            {loading ? (
                <div style={{ textAlign: 'center', padding: 50 }}>
                    <Spin size="large" tip="載入組織架構中..." />
                </div>
            ) : treeData.length > 0 ? (
                <Row gutter={24}>
                    <Col span={16}>
                        <Card title="架構樹狀圖 (右鍵可操作)" bordered={false}>
                            <Tree
                                showLine
                                switcherIcon={<DownOutlined />}
                                defaultExpandAll
                                treeData={treeData}
                                titleRender={renderNodeTitle}
                                blockNode
                            />
                        </Card>
                    </Col>
                    <Col span={8}>
                        {selectedOrg && (
                            <Card title="組織概況" bordered={false}>
                                <Statistic
                                    title="組織名稱"
                                    value={selectedOrg.organizationName}
                                    prefix={<BankOutlined />}
                                />
                                <div style={{ marginTop: 16 }}>
                                    <Statistic
                                        title="總員工數"
                                        value={selectedOrg.employeeCount || 0}
                                        prefix={<TeamOutlined />}
                                        suffix="人"
                                    />
                                </div>
                                <div style={{ marginTop: 16 }}>
                                    <Statistic
                                        title="狀態"
                                        value={selectedOrg.status === 'ACTIVE' ? '運作中' : '已停用'}
                                        valueStyle={{ color: selectedOrg.status === 'ACTIVE' ? '#3f8600' : '#cf1322' }}
                                    />
                                </div>
                            </Card>
                        )}
                        <Card title="操作說明" bordered={false} style={{ marginTop: 16 }}>
                            <p>• <b>右鍵點擊</b> 部門節點可進行編輯或刪除。</p>
                            <p>• <b>右鍵點擊</b> 組織根節點可新增一級部門。</p>
                        </Card>
                    </Col>
                </Row>
            ) : (
                <Empty description="暫無組織資料" />
            )}

            {selectedOrg && (
                <DepartmentFormModal
                    visible={modalVisible}
                    onCancel={() => setModalVisible(false)}
                    onSubmit={handleModalSubmit}
                    loading={modalLoading}
                    organizationId={selectedOrg.organizationId}
                    parentDepartmentId={targetParentId}
                    initialValues={editingDept}
                />
            )}
        </div>
    );
};
