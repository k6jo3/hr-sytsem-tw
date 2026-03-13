/**
 * Organization Tree View Component
 * Domain Code: HR02
 * Page Code: HR02-P01
 * Displays organization structure in a tree format
 */

import { BankOutlined, DeleteOutlined, DownOutlined, EditOutlined, PlusOutlined, ReloadOutlined, TeamOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Col, Dropdown, Empty, Form, Input, message, Modal, Row, Select, Spin, Statistic, Tree } from 'antd';
import { DataNode } from 'antd/es/tree';
import React, { useEffect, useMemo, useState } from 'react';
import { OrganizationApi } from '../api/OrganizationApi';
import { DepartmentDto, DepartmentRequest, OrganizationDto, OrganizationRequest } from '../api/OrganizationTypes';
import { OrganizationTreeFactory } from '../factory/OrganizationTreeFactory';
import { DepartmentFormModal } from './DepartmentFormModal';

/** 組織選項（下拉選單用） */
interface OrgOption {
    organizationId: string;
    organizationName: string;
    organizationType: string;
    parentOrganizationId?: string;
}

export const OrganizationTreeView: React.FC = () => {
    const [loading, setLoading] = useState(false);
    const [treeData, setTreeData] = useState<DataNode[]>([]);
    const [selectedOrg, setSelectedOrg] = useState<OrganizationDto | null>(null);
    const [error, setError] = useState<string | null>(null);

    // 組織列表（供下拉選單使用）
    const [orgList, setOrgList] = useState<OrgOption[]>([]);
    const [selectedOrgId, setSelectedOrgId] = useState<string | null>(null);

    // Modal State
    const [modalVisible, setModalVisible] = useState(false);
    const [modalLoading, setModalLoading] = useState(false);
    const [editingDept, setEditingDept] = useState<DepartmentDto | null>(null);
    const [targetParentId, setTargetParentId] = useState<string | undefined>(undefined);

    // 新增組織 Modal
    const [orgModalVisible, setOrgModalVisible] = useState(false);
    const [orgModalLoading, setOrgModalLoading] = useState(false);
    const [orgForm] = Form.useForm();
    const orgTypeValue = Form.useWatch('organizationType', orgForm);

    /** 母公司列表（建立子公司時選用） */
    const parentOrgOptions = useMemo(
        () => orgList.filter((o) => o.organizationType === 'PARENT'),
        [orgList]
    );

    /** 載入組織列表 */
    const loadOrgList = async (): Promise<OrgOption[]> => {
        const orgsRes = await OrganizationApi.getOrganizations();
        const orgs = orgsRes.content;
        const options: OrgOption[] = orgs.map((o) => ({
            organizationId: o.organizationId,
            organizationName: o.organizationName,
            organizationType: o.organizationType,
            parentOrganizationId: o.parentOrganizationId,
        }));
        setOrgList(options);
        return options;
    };

    /** 載入指定組織的樹狀結構 */
    const loadTreeForOrg = async (orgId: string) => {
        setLoading(true);
        setError(null);
        try {
            const treeRes = await OrganizationApi.getOrganizationTree(orgId);
            const nodes = OrganizationTreeFactory.createTreeData(treeRes.data, treeRes.departments);
            setTreeData(nodes);
            setSelectedOrg(treeRes.data);
            setSelectedOrgId(orgId);
        } catch (err) {
            console.error(err);
            setError('無法載入組織架構資料，請稍後再試');
        } finally {
            setLoading(false);
        }
    };

    /** 初始載入：取組織列表，預設選第一筆 */
    const loadOrganizationData = async () => {
        setLoading(true);
        setError(null);
        try {
            const options = await loadOrgList();
            if (options.length === 0) {
                setTreeData([]);
                setSelectedOrg(null);
                setSelectedOrgId(null);
                setLoading(false);
                return;
            }
            // 如果已選過且仍存在，維持選擇；否則預設第一筆
            const targetId = selectedOrgId && options.some((o) => o.organizationId === selectedOrgId)
                ? selectedOrgId
                : options[0]!.organizationId;
            await loadTreeForOrg(targetId);
        } catch (err) {
            console.error(err);
            setError('無法載入組織架構資料，請稍後再試');
            setLoading(false);
        }
    };

    useEffect(() => {
        loadOrganizationData();
    }, []);

    /** 切換組織 */
    const handleOrgChange = (orgId: string) => {
        loadTreeForOrg(orgId);
    };

    // Handlers
    const handleAddDepartment = (parentId?: string) => {
        setEditingDept(null);
        setTargetParentId(parentId);
        setModalVisible(true);
    };

    const handleEditDepartment = (dept: DepartmentDto) => {
        setEditingDept(dept);
        setTargetParentId(undefined);
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
                } catch (err: any) {
                    const serverMsg = err?.message || '刪除失敗，該部門可能包含子部門或員工';
                    message.error(serverMsg);
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
            throw err;
        } finally {
            setModalLoading(false);
        }
    };

    /** 新增組織 */
    const handleCreateOrganization = async (values: OrganizationRequest) => {
        setOrgModalLoading(true);
        try {
            await OrganizationApi.createOrganization(values);
            message.success('組織建立成功');
            setOrgModalVisible(false);
            orgForm.resetFields();
            loadOrganizationData();
        } catch (err: any) {
            const serverMsg = err?.message || '組織建立失敗';
            message.error(serverMsg);
        } finally {
            setOrgModalLoading(false);
        }
    };

    // Node Context Menu
    const renderNodeTitle = (node: DataNode) => {
        const deptData = (node as any).data as DepartmentDto | undefined;
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

    /** 建構分組下拉選單：母公司為群組標題，子公司列在其下 */
    const renderOrgSelect = () => {
        const parents = orgList.filter((o) => o.organizationType === 'PARENT');
        const subsidiaries = orgList.filter((o) => o.organizationType === 'SUBSIDIARY');

        return (
            <Select
                value={selectedOrgId ?? undefined}
                onChange={handleOrgChange}
                style={{ minWidth: 280 }}
                placeholder="選擇組織"
            >
                {parents.map((parent) => {
                    const children = subsidiaries.filter((s) => s.parentOrganizationId === parent.organizationId);
                    if (children.length === 0) {
                        // 母公司沒有子公司，直接顯示
                        return (
                            <Select.Option key={parent.organizationId} value={parent.organizationId}>
                                {parent.organizationName}（母公司）
                            </Select.Option>
                        );
                    }
                    // 母公司 + 其子公司一組
                    return (
                        <Select.OptGroup key={parent.organizationId} label={`${parent.organizationName}（母公司）`}>
                            <Select.Option key={parent.organizationId} value={parent.organizationId}>
                                {parent.organizationName}
                            </Select.Option>
                            {children.map((child) => (
                                <Select.Option key={child.organizationId} value={child.organizationId}>
                                    &nbsp;&nbsp;└ {child.organizationName}
                                </Select.Option>
                            ))}
                        </Select.OptGroup>
                    );
                })}
                {/* 沒有歸屬母公司的子公司（資料異常保護） */}
                {subsidiaries.filter((s) => !parents.some((p) => p.organizationId === s.parentOrganizationId)).length > 0 && (
                    <Select.OptGroup label="未歸屬子公司">
                        {subsidiaries
                            .filter((s) => !parents.some((p) => p.organizationId === s.parentOrganizationId))
                            .map((child) => (
                                <Select.Option key={child.organizationId} value={child.organizationId}>
                                    {child.organizationName}
                                </Select.Option>
                            ))}
                    </Select.OptGroup>
                )}
            </Select>
        );
    };

    return (
        <div style={{ padding: 24 }}>
            <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                    <h2 style={{ margin: 0 }}>組織架構圖</h2>
                    {orgList.length > 0 && renderOrgSelect()}
                </div>
                <div>
                    <Button
                        icon={<BankOutlined />}
                        onClick={() => setOrgModalVisible(true)}
                        style={{ marginRight: 8 }}
                    >
                        建立組織
                    </Button>
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
                <Empty description="尚未建立組織，請先建立組織後再新增部門">
                    <Button type="primary" icon={<BankOutlined />} onClick={() => setOrgModalVisible(true)}>
                        建立組織
                    </Button>
                </Empty>
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
            {/* 新增組織 Modal */}
            <Modal
                title="建立組織"
                open={orgModalVisible}
                onCancel={() => { setOrgModalVisible(false); orgForm.resetFields(); }}
                onOk={() => orgForm.submit()}
                confirmLoading={orgModalLoading}
                okText="建立"
                cancelText="取消"
            >
                <Form
                    form={orgForm}
                    layout="vertical"
                    onFinish={handleCreateOrganization}
                    initialValues={{ organizationType: 'PARENT' }}
                >
                    <Form.Item name="organizationCode" label="組織代碼" rules={[{ required: true, message: '請輸入組織代碼' }]}>
                        <Input placeholder="例如：HQ" />
                    </Form.Item>
                    <Form.Item name="organizationName" label="組織名稱" rules={[{ required: true, message: '請輸入組織名稱' }]}>
                        <Input placeholder="例如：總公司" />
                    </Form.Item>
                    <Form.Item name="organizationType" label="組織類型" rules={[{ required: true }]}>
                        <Select onChange={() => orgForm.setFieldValue('parentOrganizationId', undefined)}>
                            <Select.Option value="PARENT">母公司</Select.Option>
                            <Select.Option value="SUBSIDIARY">子公司</Select.Option>
                        </Select>
                    </Form.Item>
                    {orgTypeValue === 'SUBSIDIARY' && (
                        <Form.Item
                            name="parentOrganizationId"
                            label="所屬母公司"
                            rules={[{ required: true, message: '請選擇所屬母公司' }]}
                        >
                            <Select placeholder="選擇母公司">
                                {parentOrgOptions.map((p) => (
                                    <Select.Option key={p.organizationId} value={p.organizationId}>
                                        {p.organizationName}
                                    </Select.Option>
                                ))}
                            </Select>
                        </Form.Item>
                    )}
                    <Form.Item name="taxId" label="統一編號">
                        <Input placeholder="選填" />
                    </Form.Item>
                    <Form.Item name="address" label="地址">
                        <Input placeholder="選填" />
                    </Form.Item>
                    <Form.Item name="phoneNumber" label="電話">
                        <Input placeholder="選填" />
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};
