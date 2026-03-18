import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import { Button, Card, Input, message, Modal, Space, Table, Tabs, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import { AttendanceApi } from '../features/attendance/api/AttendanceApi';
import type {
    CorrectionApplicationDto,
    LeaveApplicationDto,
    OvertimeApplicationDto
} from '../features/attendance/api/AttendanceTypes';
import { LeaveApi } from '../features/attendance/api/LeaveApi';
import { OvertimeApi } from '../features/attendance/api/OvertimeApi';

const { TabPane } = Tabs;
const { TextArea } = Input;
const { Title } = Typography;

export const HR03ApprovalListPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('leave');
  const [loading, setLoading] = useState(false);
  
  // Data states
  const [pendingLeaves, setPendingLeaves] = useState<LeaveApplicationDto[]>([]);
  const [pendingOvertimes, setPendingOvertimes] = useState<OvertimeApplicationDto[]>([]);
  const [pendingCorrections, setPendingCorrections] = useState<CorrectionApplicationDto[]>([]);

  // Modal states
  const [rejectModalVisible, setRejectModalVisible] = useState(false);
  const [rejectReason, setRejectReason] = useState('');
  const [currentItem, setCurrentItem] = useState<{ id: string, type: 'leave' | 'overtime' | 'correction' } | null>(null);

  const fetchData = async (tab: string) => {
    setLoading(true);
    try {
      if (tab === 'leave') {
        const res = await LeaveApi.getLeaveApplications({ status: 'PENDING' });
        setPendingLeaves(res.items || []);
      } else if (tab === 'overtime') {
        const res = await OvertimeApi.getOvertimeApplications({ status: 'PENDING' });
        setPendingOvertimes(res.items || []);
      } else if (tab === 'correction') {
        const res = await AttendanceApi.getCorrectionApplications({ status: 'PENDING' });
        setPendingCorrections(res.items || []);
      }
    } catch (error: any) {
      const status = error?.response?.status;
      if (status !== 404) {
        message.error('載入資料失敗');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData(activeTab);
  }, [activeTab]);

  const handleApprove = async (id: string, type: 'leave' | 'overtime' | 'correction') => {
    try {
      if (type === 'leave') {
        await LeaveApi.approveLeave(id);
      } else if (type === 'overtime') {
        await OvertimeApi.approveOvertime(id);
      } else if (type === 'correction') {
        await AttendanceApi.approveCorrection(id);
      }
      message.success('核准成功');
      fetchData(activeTab);
    } catch (error) {
      message.error('核准失敗');
    }
  };

  const showRejectModal = (id: string, type: 'leave' | 'overtime' | 'correction') => {
    setCurrentItem({ id, type });
    setRejectReason('');
    setRejectModalVisible(true);
  };

  const handleReject = async () => {
    if (!currentItem || !rejectReason.trim()) {
      message.warning('請輸入駁回原因');
      return;
    }

    try {
      if (currentItem.type === 'leave') {
        await LeaveApi.rejectLeave(currentItem.id, rejectReason);
      } else if (currentItem.type === 'overtime') {
        await OvertimeApi.rejectOvertime(currentItem.id, rejectReason);
      } else if (currentItem.type === 'correction') {
        // Attendance doesn't have reject yet, but we'll simulate or wait for backend
        message.info('目前暫不支援補卡駁回，請聯繫系統管理員');
        setRejectModalVisible(false);
        return;
      }
      message.success('已駁回');
      setRejectModalVisible(false);
      fetchData(activeTab);
    } catch (error) {
      message.error('駁回失敗');
    }
  };

  const leaveColumns = [
    { title: '員工', dataIndex: 'employeeName', key: 'employeeName' },
    { title: '假別', dataIndex: 'leaveTypeName', key: 'leaveTypeName' },
    { title: '開始時間', dataIndex: 'startDate', key: 'startDate' },
    { title: '結束時間', dataIndex: 'endDate', key: 'endDate' },
    { title: '天數', dataIndex: 'leaveDays', key: 'leaveDays' },
    { title: '原因', dataIndex: 'reason', key: 'reason' },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: LeaveApplicationDto) => (
        <Space size="middle">
          <Button 
            type="primary" 
            icon={<CheckCircleOutlined />} 
            onClick={() => handleApprove(record.applicationId, 'leave')}
          >
            核准
          </Button>
          <Button 
            danger 
            icon={<CloseCircleOutlined />} 
            onClick={() => showRejectModal(record.applicationId, 'leave')}
          >
            駁回
          </Button>
        </Space>
      ),
    },
  ];

  const overtimeColumns = [
    { title: '員工', dataIndex: 'employeeName', key: 'employeeName' },
    { title: '加班日期', dataIndex: 'overtimeDate', key: 'overtimeDate' },
    { title: '時數', dataIndex: 'overtimeHours', key: 'overtimeHours' },
    { title: '類型', dataIndex: 'overtimeType', key: 'overtimeType' },
    { title: '原因', dataIndex: 'reason', key: 'reason' },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: OvertimeApplicationDto) => (
        <Space size="middle">
          <Button 
            type="primary" 
            icon={<CheckCircleOutlined />} 
            onClick={() => handleApprove(record.applicationId, 'overtime')}
          >
            核准
          </Button>
          <Button 
            danger 
            icon={<CloseCircleOutlined />} 
            onClick={() => showRejectModal(record.applicationId, 'overtime')}
          >
            駁回
          </Button>
        </Space>
      ),
    },
  ];

  const correctionColumns = [
    { title: '員工', dataIndex: 'employeeName', key: 'employeeName' },
    { title: '補卡日期', dataIndex: 'correctionDate', key: 'correctionDate' },
    { title: '補卡類型', dataIndex: 'correctionType', key: 'correctionType' },
    { title: '原因', dataIndex: 'reason', key: 'reason' },
    {
      title: '操作',
      key: 'action',
      render: (_: any, record: CorrectionApplicationDto) => (
        <Space size="middle">
          <Button 
            type="primary" 
            icon={<CheckCircleOutlined />} 
            onClick={() => handleApprove(record.correctionId, 'correction')}
          >
            核准
          </Button>
          <Button 
            danger 
            icon={<CloseCircleOutlined />} 
            onClick={() => showRejectModal(record.correctionId, 'correction')}
          >
            駁回
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Card>
        <Title level={2}>差勤審核中心</Title>
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          <TabPane tab="請假審核" key="leave">
            <Table
              columns={leaveColumns}
              dataSource={pendingLeaves}
              rowKey="applicationId"
              loading={loading}
              locale={{ emptyText: '目前沒有待審核的請假申請' }}
            />
          </TabPane>
          <TabPane tab="加班審核" key="overtime">
            <Table
              columns={overtimeColumns}
              dataSource={pendingOvertimes}
              rowKey="applicationId"
              loading={loading}
              locale={{ emptyText: '目前沒有待審核的加班申請' }}
            />
          </TabPane>
          <TabPane tab="補卡審核" key="correction">
            <Table
              columns={correctionColumns}
              dataSource={pendingCorrections}
              rowKey="correctionId"
              loading={loading}
              locale={{ emptyText: '目前沒有待審核的補卡申請' }}
            />
          </TabPane>
        </Tabs>
      </Card>

      <Modal
        title="駁回申請"
        visible={rejectModalVisible}
        onOk={handleReject}
        onCancel={() => setRejectModalVisible(false)}
      >
        <div style={{ marginBottom: '16px' }}>請輸入駁回原因：</div>
        <TextArea 
          rows={4} 
          value={rejectReason} 
          onChange={(e) => setRejectReason(e.target.value)}
          placeholder="必填項目"
        />
      </Modal>
    </div>
  );
};
