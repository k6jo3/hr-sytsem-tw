import { ArrowLeftOutlined, PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import { Button, Card, Col, Row, Space, Typography, message } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ProjectWbsTree } from '../features/project/components/ProjectWbsTree';
import { TaskModal } from '../features/project/components/TaskModal';
import { useProject } from '../features/project/hooks/useProject';
import type { TaskViewModel } from '../features/project/model/ProjectViewModel';

const { Title, Text } = Typography;

/**
 * HR06-P05: WBS 工項管理頁面
 */
export const HR06ProjectTasksPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { project, tasks, loading, error, fetchProject, fetchTasks, createTask, updateTaskProgress } = useProject(id);
  
  // Modal State
  const [modalVisible, setModalVisible] = useState(false);
  const [parentTask, setParentTask] = useState<TaskViewModel | null>(null);
  const [editingTask, setEditingTask] = useState<TaskViewModel | null>(null);

  useEffect(() => {
    if (id) {
      fetchProject(id);
      fetchTasks(id);
    }
  }, [id, fetchProject, fetchTasks]);

  useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error]);

  const handleAddTask = (parent: TaskViewModel | null = null) => {
    setParentTask(parent);
    setEditingTask(null);
    setModalVisible(true);
  };

  const handleEditTask = (task: TaskViewModel) => {
    setParentTask(null);
    setEditingTask(task);
    setModalVisible(true);
  };

  const handleModalSubmit = async (values: any) => {
    if (editingTask) {
      return await updateTaskProgress(editingTask.id, values.progress, values.status);
    } else {
      return await createTask(id!, values);
    }
  };

  const handleModalSuccess = () => {
    setModalVisible(false);
    fetchTasks(id!);
  };

  if (!project && loading) return <Card loading />;

  return (
    <div style={{ padding: 24 }}>
      <Space direction="vertical" size="large" style={{ width: '100%' }}>
        <Row justify="space-between" align="middle">
          <Col>
            <Space size="middle">
              <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(-1)} />
              <div>
                <Text type="secondary">{project?.projectCode}</Text>
                <Title level={3} style={{ margin: 0 }}>工項管理 (WBS)</Title>
              </div>
            </Space>
          </Col>
          <Col>
            <Space>
              <Button icon={<ReloadOutlined />} onClick={() => fetchTasks(id!)}>重新整理</Button>
              <Button type="primary" icon={<PlusOutlined />} onClick={() => handleAddTask()}>新增主工項</Button>
            </Space>
          </Col>
        </Row>

        <Card title={`${project?.projectName} - 工項結構`}>
          <ProjectWbsTree 
            tasks={tasks} 
            loading={loading} 
            onEdit={handleEditTask}
            onAddSubTask={handleAddTask}
          />
        </Card>
      </Space>

      {id && (
        <TaskModal
          visible={modalVisible}
          projectId={id}
          parentTask={parentTask}
          editingTask={editingTask}
          onCancel={() => setModalVisible(false)}
          onSuccess={handleModalSuccess}
          onSubmit={handleModalSubmit}
          loading={loading}
        />
      )}
    </div>
  );
};

export default HR06ProjectTasksPage;
