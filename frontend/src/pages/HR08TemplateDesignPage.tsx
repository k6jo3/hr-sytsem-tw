import { ArrowDownOutlined, ArrowUpOutlined, CloudUploadOutlined, DeleteOutlined, EyeOutlined, LeftOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons';
import { Button, Card, Col, Form, Input, InputNumber, message, Modal, Row, Select, Space, Switch, Table, Tag, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import type { EvaluationItemDto, UpdateTemplateRequest } from '../features/performance/api/PerformanceTypes';
import { useTemplate } from '../features/performance/hooks/useTemplate';

const { Title, Text } = Typography;
const { Option } = Select;

/**
 * HR08-P02 考核表單設計頁面
 */
export const HR08TemplateDesignPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const cycleName = location.state?.cycleName || '考核週期';
  
  const { template, saving, updateTemplate, publishTemplate } = useTemplate(id || '');
  const [form] = Form.useForm();
  
  const [items, setItems] = useState<EvaluationItemDto[]>([]);
  const [distributionRules, setDistributionRules] = useState<Record<string, number>>({
    A: 10, B: 30, C: 50, D: 8, E: 2
  });
  const [previewVisible, setPreviewVisible] = useState(false);

  useEffect(() => {
    if (template) {
      form.setFieldsValue({
        formName: template.formName,
        scoringSystem: template.scoringSystemValue,
        forcedDistribution: template.forcedDistribution,
      });
      const itemDtos: EvaluationItemDto[] = template.evaluationItems.map(vm => ({
        item_id: vm.itemId,
        item_name: vm.itemName,
        weight: vm.weight,
        max_score: vm.maxScore,
        comments: vm.comments,
      }));
      setItems(itemDtos);
      
      if (template.distributionRules) {
        setDistributionRules(template.distributionRules);
      }
    } else {
       form.setFieldsValue({
        formName: `${cycleName} 考核表`,
        scoringSystem: 'FIVE_POINT',
        forcedDistribution: true,
       });
    }
  }, [template, cycleName, form]);

  const handleAddItem = () => {
    const newItem: EvaluationItemDto = {
      item_id: `temp-${Date.now()}`,
      item_name: '新考核項目',
      weight: 0.1, 
      max_score: 5,
    };
    setItems([...items, newItem]);
  };

  const handleDeleteItem = (index: number) => {
    const newItems = [...items];
    newItems.splice(index, 1);
    setItems(newItems);
  };

  const handleMoveItem = (index: number, direction: 'up' | 'down') => {
    if (direction === 'up' && index === 0) return;
    if (direction === 'down' && index === items.length - 1) return;
    
    const newItems = [...items];
    const targetIndex = direction === 'up' ? index - 1 : index + 1;
    const currentItem = newItems[index];
    const targetItem = newItems[targetIndex];
    
    if (currentItem && targetItem) {
      newItems[index] = targetItem;
      newItems[targetIndex] = currentItem;
      setItems(newItems);
    }
  };

  const handleItemChange = (index: number, field: keyof EvaluationItemDto, value: any) => {
    const newItems = [...items];
    // Cast to any to avoid TS error with partial updates on strict object types
    newItems[index] = { ...newItems[index], [field]: value } as EvaluationItemDto;
    setItems(newItems);
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      
      const totalWeight = items.reduce((sum, item) => sum + item.weight, 0);
      if (Math.abs(totalWeight - 1.0) > 0.001) {
        message.error(`總權重必須為 100% (目前: ${(totalWeight * 100).toFixed(1)}%)`);
        return;
      }

      const request: UpdateTemplateRequest = {
        form_name: values.formName,
        scoring_system: values.scoringSystem,
        forced_distribution: values.forcedDistribution,
        distribution_rules: values.forcedDistribution ? distributionRules : undefined,
        evaluation_items: items,
      };

      await updateTemplate(request);
    } catch (error) {
      console.error(error);
    }
  };

  const handlePublish = () => {
    Modal.confirm({
      title: '確認發布',
      content: '發布後將無法修改表單設定，確定要發布嗎？',
      onOk: async () => {
        await publishTemplate();
      }
    });
  };

  const columns = [
    {
      title: '考核項目',
      dataIndex: 'item_name',
      key: 'item_name',
      render: (text: string, _record: EvaluationItemDto, index: number) => (
        <Input 
          value={text} 
          onChange={e => handleItemChange(index, 'item_name', e.target.value)} 
        />
      ),
    },
    {
      title: '權重 (%)',
      dataIndex: 'weight',
      key: 'weight',
      width: 120,
      render: (value: number, _record: EvaluationItemDto, index: number) => (
        <InputNumber
          min={0}
          max={100}
          value={Math.round(value * 100)}
          formatter={v => `${v}%`}
          parser={v => Number(v?.replace('%', ''))}
          onChange={v => handleItemChange(index, 'weight', (v || 0) / 100)}
        />
      ),
    },
    {
      title: '說明',
      dataIndex: 'comments',
      key: 'comments',
      render: (text: string, _record: EvaluationItemDto, index: number) => (
        <Input 
          value={text} 
          placeholder="評分標準說明"
          onChange={e => handleItemChange(index, 'comments', e.target.value)} 
        />
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: any, _record: EvaluationItemDto, index: number) => (
        <Space size="small">
          <Button 
            icon={<ArrowUpOutlined />} 
            size="small" 
            disabled={index === 0}
            onClick={() => handleMoveItem(index, 'up')}
          />
          <Button 
            icon={<ArrowDownOutlined />} 
            size="small" 
            disabled={index === items.length - 1}
            onClick={() => handleMoveItem(index, 'down')}
          />
          <Button 
            icon={<DeleteOutlined />} 
            size="small" 
            danger 
            onClick={() => handleDeleteItem(index)}
          />
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        {/* Header */}
        <Row justify="space-between" align="middle">
          <Col>
            <Space>
              <Button icon={<LeftOutlined />} onClick={() => navigate('/admin/performance')} />
              <Title level={2} style={{ margin: 0 }}>考核表單設計</Title>
              <Tag color="blue">{cycleName}</Tag>
            </Space>
          </Col>
          <Col>
            <Space>
              <Button icon={<EyeOutlined />} onClick={() => setPreviewVisible(true)}>預覽</Button>
              <Button icon={<SaveOutlined />} type="primary" onClick={handleSave} loading={saving}>儲存草稿</Button>
              <Button icon={<CloudUploadOutlined />} type="primary" ghost onClick={handlePublish} loading={saving}>發布</Button>
            </Space>
          </Col>
        </Row>

        {/* Basic Config */}
        <Card title="基本設定" size="small">
          <Form form={form} layout="vertical">
            <Row gutter={24}>
              <Col span={8}>
                <Form.Item name="formName" label="表單名稱" rules={[{ required: true }]}>
                  <Input />
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item name="scoringSystem" label="評分制度" rules={[{ required: true }]}>
                  <Select>
                    <Option value="FIVE_POINT">五分制 (1-5)</Option>
                    <Option value="FIVE_LEVEL">五等第 (A-E)</Option>
                    <Option value="PERCENTAGE">百分制 (0-100)</Option>
                  </Select>
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item name="forcedDistribution" label="強制分配" valuePropName="checked">
                  <Switch checkedChildren="啟用" unCheckedChildren="停用" />
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Card>

        {/* Evaluation Items */}
        <Card title="考核項目設定" size="small">
          <Table
            dataSource={items}
            columns={columns}
            rowKey="item_id"
            pagination={false}
            footer={() => (
              <Button type="dashed" onClick={handleAddItem} style={{ width: '100%' }} icon={<PlusOutlined />}>
                新增考核項目
              </Button>
            )}
          />
          <div style={{ marginTop: 16, textAlign: 'right' }}>
            <Text strong>總權重: {(items.reduce((sum, item) => sum + item.weight, 0) * 100).toFixed(0)}%</Text>
          </div>
        </Card>
      </Space>

      {/* Preview Modal */}
      <Modal
        title="表單預覽"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={null}
        width={800}
      >
        <div style={{ padding: 24 }}>
          <Title level={3} style={{ textAlign: 'center' }}>{form.getFieldValue('formName')}</Title>
          <Table
             dataSource={items}
             columns={[
               { title: '考核項目', dataIndex: 'item_name', key: 'name' },
               { title: '說明', dataIndex: 'comments', key: 'comments' },
               { title: '權重', dataIndex: 'weight', key: 'weight', render: (v: number) => `${Math.round(v*100)}%` },
               { title: '自評', key: 'eval', render: () => <Input style={{ width: 80 }} disabled placeholder="分數" /> }
             ]}
             pagination={false}
             rowKey="item_id"
             bordered
          />
        </div>
      </Modal>
    </div>
  );
};
