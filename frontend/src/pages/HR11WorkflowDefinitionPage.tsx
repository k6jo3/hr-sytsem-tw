import React, { useState } from 'react';
import { WorkflowDefinitionList, WorkflowDesigner } from '../features/workflow/components';

/**
 * HR11-P01: 流程定義管理頁面
 * Feature: workflow
 */
export const HR11WorkflowDefinitionPage: React.FC = () => {
  const [mode, setMode] = useState<'list' | 'design'>('list');
  const [editingId, setEditingId] = useState<string | undefined>(undefined);

  const handleDesign = (definitionId: string) => {
    setEditingId(definitionId);
    setMode('design');
  };

  const handleCreate = () => {
    setEditingId(undefined);
    setMode('design');
  };

  const handleBack = () => {
    setMode('list');
    setEditingId(undefined);
  };

  if (mode === 'design') {
    return (
      <div style={{ padding: 24 }}>
        <WorkflowDesigner
          definitionId={editingId}
          onSaved={handleBack}
          onBack={handleBack}
        />
      </div>
    );
  }

  return (
    <div style={{ padding: 24 }}>
      <WorkflowDefinitionList onDesign={handleDesign} onCreate={handleCreate} />
    </div>
  );
};
