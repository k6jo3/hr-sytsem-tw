/**
 * 視覺化流程設計器元件
 * Domain Code: HR11
 *
 * 使用 Ant Design 表格 + 拖拉模擬設計器
 * （完整版可整合 ReactFlow，此處提供可用的表格式設計器）
 */

import {
  DeleteOutlined,
  PlusOutlined,
  SaveOutlined,
} from '@ant-design/icons';
import {
  Button,
  Card,
  Form,
  Input,
  message,
  Modal,
  Select,
  Space,
  Table,
  Tag,
  Typography,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useCallback, useEffect, useState } from 'react';
import { WorkflowApi } from '../api';
import type { WorkflowNodeDto, WorkflowEdgeDto, NodeType } from '../api/WorkflowTypes';

const { Title, Text } = Typography;

interface WorkflowDesignerProps {
  definitionId?: string;
  onSaved?: () => void;
  onBack?: () => void;
}

interface EditableNode {
  key: string;
  node_id: string;
  node_type: NodeType;
  node_name: string;
  assignee_type?: string;
  assignee_ids?: string[];
  condition?: string;
}

const NODE_TYPE_OPTIONS = [
  { value: 'START', label: '開始節點', color: 'blue' },
  { value: 'APPROVAL', label: '審核節點', color: 'orange' },
  { value: 'CONDITION', label: '條件分流', color: 'purple' },
  { value: 'PARALLEL', label: '平行會簽', color: 'cyan' },
  { value: 'END', label: '結束節點', color: 'green' },
];

const ASSIGNEE_TYPE_OPTIONS = [
  { value: 'ROLE', label: '依角色' },
  { value: 'USER', label: '指定人員' },
  { value: 'DEPARTMENT', label: '依部門' },
];

export const WorkflowDesigner: React.FC<WorkflowDesignerProps> = ({
  definitionId,
  onSaved,
  onBack,
}) => {
  const [flowName, setFlowName] = useState('');
  const [flowType, setFlowType] = useState('');
  const [nodes, setNodes] = useState<EditableNode[]>([
    { key: 'start', node_id: 'start', node_type: 'START', node_name: '開始' },
    { key: 'end', node_id: 'end', node_type: 'END', node_name: '結束' },
  ]);
  const [saving, setSaving] = useState(false);
  const [nodeModalVisible, setNodeModalVisible] = useState(false);
  const [nodeForm] = Form.useForm();

  const loadDefinition = useCallback(async (id: string) => {
    try {
      const response = await WorkflowApi.getDefinitions();
      const definition = response.data.find((d) => d.definition_id === id);
      if (definition) {
        setFlowName(definition.flow_name);
        setFlowType(definition.flow_type);
        const loadedNodes = definition.nodes.map((n, i) => ({
          ...n,
          key: n.node_id || `node-${i}`,
        }));
        if (loadedNodes.length > 0) {
          setNodes(loadedNodes);
        }
      }
    } catch {
      message.error('載入流程定義失敗');
    }
  }, []);

  useEffect(() => {
    if (definitionId) {
      loadDefinition(definitionId);
    }
  }, [definitionId, loadDefinition]);

  const handleAddNode = async () => {
    try {
      const values = await nodeForm.validateFields();
      const newNode: EditableNode = {
        key: `node-${Date.now()}`,
        node_id: `node-${Date.now()}`,
        node_type: values.node_type,
        node_name: values.node_name,
        assignee_type: values.assignee_type,
        condition: values.condition,
      };
      // 插入到 END 節點之前
      const endIndex = nodes.findIndex((n) => n.node_type === 'END');
      const newNodes = [...nodes];
      newNodes.splice(endIndex, 0, newNode);
      setNodes(newNodes);
      setNodeModalVisible(false);
      nodeForm.resetFields();
    } catch {
      // 驗證失敗
    }
  };

  const handleRemoveNode = (nodeId: string) => {
    setNodes(nodes.filter((n) => n.node_id !== nodeId));
  };

  const handleSave = async () => {
    if (!flowName.trim()) {
      message.warning('請輸入流程名稱');
      return;
    }
    if (!flowType) {
      message.warning('請選擇流程類型');
      return;
    }

    setSaving(true);
    try {
      const nodesDtos: WorkflowNodeDto[] = nodes.map((n) => ({
        node_id: n.node_id,
        node_type: n.node_type,
        node_name: n.node_name,
        assignee_type: n.assignee_type as any,
        assignee_ids: n.assignee_ids,
        condition: n.condition,
      }));

      // 自動建立順序連線
      const edgesDtos: WorkflowEdgeDto[] = nodes.slice(0, -1).map((n, i) => {
        const nextNode = nodes[i + 1];
        return {
          edge_id: `edge-${i}`,
          source_node: n.node_id,
          target_node: nextNode?.node_id ?? '',
          condition: nextNode?.node_type === 'CONDITION' ? nextNode.condition : undefined,
        };
      });

      await WorkflowApi.createDefinition({
        flow_name: flowName,
        flow_type: flowType,
        nodes: nodesDtos,
        edges: edgesDtos,
      });
      message.success('流程定義儲存成功');
      onSaved?.();
    } catch {
      message.error('儲存失敗');
    } finally {
      setSaving(false);
    }
  };

  const nodeColumns: ColumnsType<EditableNode> = [
    {
      title: '順序',
      key: 'order',
      width: 60,
      align: 'center',
      render: (_: unknown, __: unknown, index: number) => index + 1,
    },
    {
      title: '節點名稱',
      dataIndex: 'node_name',
      key: 'node_name',
      width: 200,
    },
    {
      title: '節點類型',
      dataIndex: 'node_type',
      key: 'node_type',
      width: 120,
      render: (type: NodeType) => {
        const option = NODE_TYPE_OPTIONS.find((o) => o.value === type);
        return <Tag color={option?.color}>{option?.label ?? type}</Tag>;
      },
    },
    {
      title: '審核人類型',
      dataIndex: 'assignee_type',
      key: 'assignee_type',
      width: 120,
      render: (type: string) => {
        if (!type) return '-';
        const option = ASSIGNEE_TYPE_OPTIONS.find((o) => o.value === type);
        return option?.label ?? type;
      },
    },
    {
      title: '條件',
      dataIndex: 'condition',
      key: 'condition',
      ellipsis: true,
      render: (text: string) => text || '-',
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_: unknown, record: EditableNode) => {
        if (record.node_type === 'START' || record.node_type === 'END') return null;
        return (
          <Button
            type="link"
            danger
            size="small"
            icon={<DeleteOutlined />}
            onClick={() => handleRemoveNode(record.node_id)}
          >
            刪除
          </Button>
        );
      },
    },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
          <div>
            <Title level={4} style={{ margin: 0 }}>
              {definitionId ? '編輯流程定義' : '新增流程定義'}
            </Title>
            <Text type="secondary">設計審核流程的節點與連線</Text>
          </div>
          <Space>
            {onBack && <Button onClick={onBack}>返回列表</Button>}
            <Button type="primary" icon={<SaveOutlined />} onClick={handleSave} loading={saving}>
              儲存
            </Button>
          </Space>
        </div>

        <Space style={{ marginBottom: 16 }} size="large">
          <div>
            <Text strong>流程名稱：</Text>
            <Input
              value={flowName}
              onChange={(e) => setFlowName(e.target.value)}
              placeholder="例：請假審核流程"
              style={{ width: 200 }}
            />
          </div>
          <div>
            <Text strong>流程類型：</Text>
            <Select
              value={flowType || undefined}
              onChange={setFlowType}
              placeholder="選擇類型"
              style={{ width: 160 }}
              options={[
                { value: 'LEAVE', label: '請假流程' },
                { value: 'OVERTIME', label: '加班流程' },
                { value: 'EXPENSE', label: '費用流程' },
                { value: 'RECRUITMENT', label: '招募流程' },
                { value: 'PURCHASE', label: '採購流程' },
              ]}
            />
          </div>
        </Space>
      </Card>

      <Card
        title="流程節點"
        extra={
          <Button type="dashed" icon={<PlusOutlined />} onClick={() => setNodeModalVisible(true)}>
            新增節點
          </Button>
        }
      >
        <Table
          columns={nodeColumns}
          dataSource={nodes}
          rowKey="key"
          pagination={false}
          size="small"
        />

        {/* 視覺化流程圖預覽 */}
        <div style={{ marginTop: 24, padding: 16, background: '#fafafa', borderRadius: 8 }}>
          <Text strong>流程預覽：</Text>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8, flexWrap: 'wrap' }}>
            {nodes.map((node, index) => (
              <React.Fragment key={node.key}>
                <Tag
                  color={NODE_TYPE_OPTIONS.find((o) => o.value === node.node_type)?.color}
                  style={{ fontSize: 13, padding: '4px 12px' }}
                >
                  {node.node_name}
                </Tag>
                {index < nodes.length - 1 && <span style={{ color: '#999' }}>→</span>}
              </React.Fragment>
            ))}
          </div>
        </div>
      </Card>

      <Modal
        title="新增流程節點"
        open={nodeModalVisible}
        onOk={handleAddNode}
        onCancel={() => {
          setNodeModalVisible(false);
          nodeForm.resetFields();
        }}
        okText="確認"
        cancelText="取消"
      >
        <Form form={nodeForm} layout="vertical">
          <Form.Item
            name="node_name"
            label="節點名稱"
            rules={[{ required: true, message: '請輸入節點名稱' }]}
          >
            <Input placeholder="例：主管審核" />
          </Form.Item>
          <Form.Item
            name="node_type"
            label="節點類型"
            rules={[{ required: true, message: '請選擇節點類型' }]}
          >
            <Select
              placeholder="選擇節點類型"
              options={NODE_TYPE_OPTIONS.filter((o) => o.value !== 'START' && o.value !== 'END')}
            />
          </Form.Item>
          <Form.Item name="assignee_type" label="審核人類型">
            <Select placeholder="選擇審核人類型" allowClear options={ASSIGNEE_TYPE_OPTIONS} />
          </Form.Item>
          <Form.Item name="condition" label="條件表達式">
            <Input placeholder="例：totalDays > 3" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};
