import React, { useState } from 'react';
import { Card, Form, InputNumber, Input, Button, Table, Space, Alert, Statistic } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import type { EvaluationItemViewModel } from '../model/PerformanceViewModel';
import type { EvaluationItemDto } from '../api/PerformanceTypes';

const { TextArea } = Input;

export interface SelfEvaluationFormProps {
  evaluationItems: EvaluationItemViewModel[];
  comments?: string;
  onSave: (items: EvaluationItemDto[], comments?: string) => Promise<void>;
  onSubmit: () => Promise<void>;
  canEdit: boolean;
  canSubmit: boolean;
  loading?: boolean;
}

interface EditableItem extends EvaluationItemViewModel {
  isEditing?: boolean;
}

/**
 * 自我評估表單組件
 */
export const SelfEvaluationForm: React.FC<SelfEvaluationFormProps> = ({
  evaluationItems,
  comments: initialComments,
  onSave,
  onSubmit,
  canEdit,
  canSubmit,
  loading: _loading = false,
}) => {
  const [items, setItems] = useState<EditableItem[]>(evaluationItems);
  const [comments, setComments] = useState(initialComments || '');
  const [saving, setSaving] = useState(false);

  const handleScoreChange = (itemId: string, score: number) => {
    setItems((prev) =>
      prev.map((item) => (item.itemId === itemId ? { ...item, score, isEditing: true } : item))
    );
  };

  const handleCommentsChange = (itemId: string, comments: string) => {
    setItems((prev) =>
      prev.map((item) =>
        item.itemId === itemId ? { ...item, comments, isEditing: true } : item
      )
    );
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const dtoItems: EvaluationItemDto[] = items.map((item) => ({
        item_id: item.itemId,
        item_name: item.itemName,
        weight: item.weight,
        score: item.score,
        comments: item.comments,
        max_score: item.maxScore,
      }));

      await onSave(dtoItems, comments);
      setItems((prev) => prev.map((item) => ({ ...item, isEditing: false })));
    } finally {
      setSaving(false);
    }
  };

  const handleSubmit = async () => {
    setSaving(true);
    try {
      await onSubmit();
    } finally {
      setSaving(false);
    }
  };

  // 計算總分
  const totalScore = items.reduce((sum, item) => {
    if (item.score !== undefined) {
      return sum + item.weight * item.score;
    }
    return sum;
  }, 0);

  const allItemsScored = items.every((item) => item.score !== undefined && item.score > 0);

  const columns: ColumnsType<EditableItem> = [
    {
      title: '評估項目',
      dataIndex: 'itemName',
      key: 'itemName',
      width: 200,
      fixed: 'left',
    },
    {
      title: '權重',
      dataIndex: 'weightDisplay',
      key: 'weight',
      width: 80,
      align: 'center',
    },
    {
      title: '分數 (1-5)',
      dataIndex: 'score',
      key: 'score',
      width: 120,
      align: 'center',
      render: (_: number, record: EditableItem) =>
        canEdit ? (
          <InputNumber
            min={1}
            max={record.maxScore}
            value={record.score}
            onChange={(value) => handleScoreChange(record.itemId, value || 0)}
            style={{ width: '100%' }}
          />
        ) : (
          record.scoreDisplay
        ),
    },
    {
      title: '自評說明',
      dataIndex: 'comments',
      key: 'comments',
      width: 300,
      render: (_: string, record: EditableItem) =>
        canEdit ? (
          <TextArea
            value={record.comments}
            onChange={(e) => handleCommentsChange(record.itemId, e.target.value)}
            placeholder="請說明您的自評理由"
            autoSize={{ minRows: 2, maxRows: 4 }}
          />
        ) : (
          record.comments || '-'
        ),
    },
    {
      title: '加權分數',
      dataIndex: 'weightedScoreDisplay',
      key: 'weightedScore',
      width: 100,
      align: 'right',
      render: (_: string, record: EditableItem) => {
        if (record.score !== undefined) {
          const weighted = record.weight * record.score;
          return weighted.toFixed(1);
        }
        return '-';
      },
    },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      {!allItemsScored && canEdit && (
        <Alert
          message="請為所有評估項目評分"
          description="完成所有項目評分後才能儲存或提交"
          type="warning"
          showIcon
        />
      )}

      <Card
        title="自我評估"
        extra={
          <Statistic
            title="加權總分"
            value={totalScore.toFixed(1)}
            precision={1}
            suffix="/ 5.0"
            valueStyle={{ color: totalScore >= 4 ? '#3f8600' : '#cf1322' }}
          />
        }
      >
        <Table
          dataSource={items}
          columns={columns}
          rowKey="itemId"
          pagination={false}
          bordered
          size="middle"
          scroll={{ x: 1000 }}
        />

        {canEdit && (
          <div style={{ marginTop: 16 }}>
            <Form.Item label="整體評語">
              <TextArea
                value={comments}
                onChange={(e) => setComments(e.target.value)}
                placeholder="請輸入您的整體自評說明"
                rows={4}
              />
            </Form.Item>
          </div>
        )}

        {canEdit && (
          <div style={{ marginTop: 16, textAlign: 'right' }}>
            <Space>
              <Button onClick={handleSave} loading={saving} disabled={!allItemsScored}>
                儲存草稿
              </Button>
              <Button
                type="primary"
                onClick={handleSubmit}
                loading={saving}
                disabled={!canSubmit || !allItemsScored}
              >
                送出自評
              </Button>
            </Space>
          </div>
        )}
      </Card>
    </div>
  );
};
