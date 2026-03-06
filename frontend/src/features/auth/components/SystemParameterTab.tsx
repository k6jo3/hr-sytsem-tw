/**
 * 系統參數管理 Tab
 * Domain Code: HR01
 */

import { Button, Card, Input, message, Space, Table, Tag, Tooltip, Typography } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useState } from 'react';
import type { SystemParameterViewModel } from '../model/SystemViewModel';

const { Text } = Typography;

interface SystemParameterTabProps {
  parameters: SystemParameterViewModel[];
  loading: boolean;
  onUpdate: (paramCode: string, paramValue: string) => Promise<void>;
  onRefresh: () => Promise<void>;
}

/** 員編相關參數代碼 */
const EMPLOYEE_NUMBER_PARAMS = ['EMPLOYEE_NUMBER_PREFIX', 'EMPLOYEE_NUMBER_FORMAT', 'EMPLOYEE_NUMBER_SEQ_DIGITS'];

export const SystemParameterTab: React.FC<SystemParameterTabProps> = ({
  parameters, loading, onUpdate, onRefresh,
}) => {
  const [editingKey, setEditingKey] = useState<string>('');
  const [editValue, setEditValue] = useState<string>('');

  const startEdit = (record: SystemParameterViewModel) => {
    setEditingKey(record.paramCode);
    setEditValue(record.paramValue);
  };

  const cancelEdit = () => {
    setEditingKey('');
    setEditValue('');
  };

  const saveEdit = async (paramCode: string) => {
    try {
      await onUpdate(paramCode, editValue);
      setEditingKey('');
      setEditValue('');
      message.success('參數更新成功');
    } catch {
      message.error('參數更新失敗');
    }
  };

  const columns: ColumnsType<SystemParameterViewModel> = [
    {
      title: '參數代碼',
      dataIndex: 'paramCode',
      key: 'paramCode',
      width: 250,
      render: (text: string) => <Text code>{text}</Text>,
    },
    {
      title: '參數名稱',
      dataIndex: 'paramName',
      key: 'paramName',
      width: 160,
    },
    {
      title: '參數值',
      dataIndex: 'paramValue',
      key: 'paramValue',
      width: 200,
      render: (_: string, record: SystemParameterViewModel) => {
        if (editingKey === record.paramCode) {
          return (
            <Space>
              <Input
                size="small"
                value={editValue}
                onChange={e => setEditValue(e.target.value)}
                onPressEnter={() => saveEdit(record.paramCode)}
                style={{ width: 120 }}
              />
              <Button size="small" type="primary" onClick={() => saveEdit(record.paramCode)}>儲存</Button>
              <Button size="small" onClick={cancelEdit}>取消</Button>
            </Space>
          );
        }
        return (
          <Space>
            <Text strong={record.isModified}>{record.paramValue}</Text>
            {record.isModified && (
              <Tooltip title={`預設值: ${record.defaultValue}`}>
                <Tag color="warning">已修改</Tag>
              </Tooltip>
            )}
          </Space>
        );
      },
    },
    {
      title: '類型',
      dataIndex: 'paramType',
      key: 'paramType',
      width: 90,
      render: (text: string) => <Tag>{text}</Tag>,
    },
    {
      title: '模組',
      dataIndex: 'moduleLabel',
      key: 'moduleLabel',
      width: 100,
    },
    {
      title: '分類',
      dataIndex: 'categoryLabel',
      key: 'categoryLabel',
      width: 100,
      render: (text: string, record: SystemParameterViewModel) => (
        <Tag color={record.categoryColor}>{text}</Tag>
      ),
    },
    {
      title: '說明',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_: unknown, record: SystemParameterViewModel) => (
        editingKey !== record.paramCode ? (
          <Button size="small" type="link" onClick={() => startEdit(record)}>編輯</Button>
        ) : null
      ),
    },
  ];

  // 員編相關參數分組
  const empParams = parameters.filter(p => EMPLOYEE_NUMBER_PARAMS.includes(p.paramCode));
  const otherParams = parameters.filter(p => !EMPLOYEE_NUMBER_PARAMS.includes(p.paramCode));

  return (
    <Space direction="vertical" style={{ width: '100%' }} size="large">
      {empParams.length > 0 && (
        <Card
          title="員工編號規則"
          size="small"
          style={{ borderColor: '#667eea' }}
          headStyle={{ background: '#f0f2ff' }}
        >
          <Table
            columns={columns}
            dataSource={empParams}
            rowKey="paramCode"
            pagination={false}
            size="small"
            loading={loading}
          />
        </Card>
      )}

      <Card
        title="一般系統參數"
        size="small"
        extra={<Button size="small" onClick={onRefresh}>重新整理</Button>}
      >
        <Table
          columns={columns}
          dataSource={otherParams}
          rowKey="paramCode"
          pagination={false}
          size="small"
          loading={loading}
        />
      </Card>
    </Space>
  );
};
