import React from 'react';
import { Card, Alert, Descriptions, Spin, Table, Tag, Space, Statistic, Row, Col } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { SelfEvaluationForm } from '../features/performance/components/SelfEvaluationForm';
import { useMyPerformance } from '../features/performance/hooks/useMyPerformance';
import type { PerformanceReviewViewModel } from '../features/performance/model/PerformanceViewModel';

/**
 * HR08-P01 我的考核（員工自助服務）
 */
export const HR08MyPerformancePage: React.FC = () => {
  const { performance, loading, error, submitting, refresh, saveReview, submitReview } =
    useMyPerformance();

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '100px 0' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error) {
    return (
      <Card>
        <Alert
          message="載入失敗"
          description={error}
          type="error"
          showIcon
          style={{ marginBottom: 16 }}
        />
      </Card>
    );
  }

  if (!performance) {
    return (
      <Card>
        <div style={{ textAlign: 'center', padding: '50px 0', color: '#999' }}>
          查無考核資料
        </div>
      </Card>
    );
  }

  // History table columns
  const historyColumns: ColumnsType<PerformanceReviewViewModel> = [
    {
      title: '考核週期',
      dataIndex: 'cycleName',
      key: 'cycleName',
      width: 200,
    },
    {
      title: '考核類型',
      dataIndex: 'reviewTypeLabel',
      key: 'reviewType',
      width: 100,
    },
    {
      title: '狀態',
      dataIndex: 'statusLabel',
      key: 'status',
      width: 100,
      render: (text: string, record: PerformanceReviewViewModel) => (
        <Tag color={record.statusColor}>{text}</Tag>
      ),
    },
    {
      title: '總分',
      dataIndex: 'overallScoreDisplay',
      key: 'overallScore',
      width: 100,
      align: 'right',
    },
    {
      title: '評等',
      dataIndex: 'overallRatingDisplay',
      key: 'overallRating',
      width: 120,
      render: (text: string, record: PerformanceReviewViewModel) =>
        text !== '-' ? <Tag color={record.overallRatingColor}>{text}</Tag> : '-',
    },
    {
      title: '提交時間',
      dataIndex: 'submittedAtDisplay',
      key: 'submittedAt',
      width: 180,
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 24 }}>
        <h1 style={{ fontSize: 24, fontWeight: 600, marginBottom: 8 }}>我的考核</h1>
        <p style={{ color: '#666', margin: 0 }}>查看當前考核週期與歷史考核記錄</p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
        {/* Current Cycle Status */}
        <Card title="考核週期資訊" size="small">
          <Alert
            message={performance.statusMessage}
            description={performance.nextAction}
            type={performance.statusType}
            showIcon
            style={{ marginBottom: 16 }}
          />

          {performance.currentCycle && (
            <>
              <Descriptions column={2} bordered size="small">
                <Descriptions.Item label="週期名稱">
                  {performance.currentCycle.cycleName}
                </Descriptions.Item>
                <Descriptions.Item label="考核類型">
                  <Tag color={performance.currentCycle.cycleTypeColor}>
                    {performance.currentCycle.cycleTypeLabel}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="考核期間">
                  {performance.currentCycle.periodDisplay}
                </Descriptions.Item>
                <Descriptions.Item label="狀態">
                  <Tag color={performance.currentCycle.statusColor}>
                    {performance.currentCycle.statusLabel}
                  </Tag>
                </Descriptions.Item>
                {performance.currentCycle.selfEvalDeadlineDisplay && (
                  <Descriptions.Item label="自評截止日期">
                    {performance.currentCycle.selfEvalDeadlineDisplay}
                  </Descriptions.Item>
                )}
                {performance.currentCycle.daysRemainingDisplay && (
                  <Descriptions.Item label="剩餘天數">
                    {performance.currentCycle.daysRemainingDisplay}
                  </Descriptions.Item>
                )}
              </Descriptions>
            </>
          )}
        </Card>

        {/* Self Evaluation Form */}
        {performance.selfReview && (
          <SelfEvaluationForm
            evaluationItems={performance.selfReview.evaluationItems}
            comments={performance.selfReview.comments}
            onSave={saveReview}
            onSubmit={submitReview}
            canEdit={performance.selfReview.canEdit}
            canSubmit={performance.selfReview.canSubmit}
            loading={submitting}
          />
        )}

        {/* Manager Review Results */}
        {performance.managerReview && (
          <Card title="主管評核結果" size="small">
            <Row gutter={16} style={{ marginBottom: 16 }}>
              <Col span={8}>
                <Statistic
                  title="總分"
                  value={performance.managerReview.overallScoreDisplay}
                  precision={1}
                  suffix="/ 5.0"
                />
              </Col>
              <Col span={8}>
                <Statistic
                  title="評等"
                  value={performance.managerReview.overallRatingDisplay}
                  valueStyle={{
                    color:
                      performance.managerReview.overallRatingColor === 'success'
                        ? '#3f8600'
                        : performance.managerReview.overallRatingColor === 'error'
                          ? '#cf1322'
                          : '#1890ff',
                  }}
                />
              </Col>
              <Col span={8}>
                <Statistic title="狀態" value={performance.managerReview.statusLabel} />
              </Col>
            </Row>

            {performance.managerReview.comments && (
              <div>
                <h4>主管評語</h4>
                <Alert message={performance.managerReview.comments} type="info" />
              </div>
            )}

            {performance.managerReview.evaluationItems.length > 0 && (
              <div style={{ marginTop: 16 }}>
                <h4>評估明細</h4>
                <Table
                  dataSource={performance.managerReview.evaluationItems}
                  columns={[
                    {
                      title: '評估項目',
                      dataIndex: 'itemName',
                      key: 'itemName',
                    },
                    {
                      title: '權重',
                      dataIndex: 'weightDisplay',
                      key: 'weight',
                      width: 80,
                      align: 'center',
                    },
                    {
                      title: '分數',
                      dataIndex: 'scoreDisplay',
                      key: 'score',
                      width: 80,
                      align: 'center',
                    },
                    {
                      title: '加權分數',
                      dataIndex: 'weightedScoreDisplay',
                      key: 'weightedScore',
                      width: 100,
                      align: 'right',
                    },
                    {
                      title: '評語',
                      dataIndex: 'comments',
                      key: 'comments',
                      ellipsis: true,
                      render: (text: string) => text || '-',
                    },
                  ]}
                  rowKey="itemId"
                  pagination={false}
                  size="small"
                />
              </div>
            )}
          </Card>
        )}

        {/* History */}
        {performance.history.length > 0 && (
          <Card title="歷史考核記錄" size="small">
            <Table
              dataSource={performance.history}
              columns={historyColumns}
              rowKey="reviewId"
              pagination={performance.history.length > 10 ? { pageSize: 10 } : false}
              size="small"
              locale={{ emptyText: '查無歷史記錄' }}
            />
          </Card>
        )}
      </div>
    </div>
  );
};
