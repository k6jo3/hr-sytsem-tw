/**
 * Employee Detail Page
 * Domain Code: HR02
 * Page Code: HR02-P04
 * Displays employee details by ID
 */

import { ArrowLeftOutlined } from '@ant-design/icons';
import { OrganizationApi } from '@features/organization/api/OrganizationApi';
import { EmployeeDto } from '@features/organization/api/OrganizationTypes';
import { EmployeeDetailView } from '@features/organization/components/EmployeeDetailView';
import { Alert, Breadcrumb, Button, Spin } from 'antd';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { PageLayout } from '@shared/components';

export const HR02EmployeeDetailPage: React.FC = () => {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const [employee, setEmployee] = useState<EmployeeDto | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const loadEmployee = async (empId: string) => {
        setLoading(true);
        setError(null);
        try {
            const res = await OrganizationApi.getEmployeeDetail(empId);
            setEmployee(res.employee);
        } catch (err) {
            console.error(err);
            setError('無法載入員工資料，請稍後再試');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (id) {
            loadEmployee(id);
        } else {
            setError('無效的員工 ID');
        }
    }, [id]);

    const handleBack = () => {
        navigate('/admin/employees');
    };

    return (
        <PageLayout>
            <div style={{ padding: '0 24px' }}>
                <Breadcrumb style={{ margin: '16px 0' }}>
                    <Breadcrumb.Item>
                        <span onClick={handleBack} style={{ cursor: 'pointer' }}>員工管理</span>
                    </Breadcrumb.Item>
                    <Breadcrumb.Item>員工詳情</Breadcrumb.Item>
                </Breadcrumb>
                
                <div style={{ marginBottom: 16 }}>
                    <Button icon={<ArrowLeftOutlined />} onClick={handleBack}>
                        返回列表
                    </Button>
                </div>

                {error ? (
                    <Alert message={error} type="error" showIcon style={{ marginBottom: 16 }} />
                ) : loading ? (
                    <div style={{ textAlign: 'center', padding: 50 }}>
                        <Spin size="large" tip="載入員工資料中..." />
                    </div>
                ) : employee ? (
                    <EmployeeDetailView employee={employee} />
                ) : null}
            </div>
        </PageLayout>
    );
};
