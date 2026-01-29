-- Organization 測試資料
-- 用於 QueryEngine 契約測試與業務合約測試

-- 清除舊資料
DELETE FROM employees;
DELETE FROM departments;

-- 部門測試資料 (共 6 筆)
INSERT INTO departments (department_id, code, name, parent_id, manager_id, status, level, sort_order, created_at, updated_at, is_deleted) VALUES
('D001', 'RD', '研發部', NULL, NULL, 'ACTIVE', 1, 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),
('D002', 'SALES', '業務部', NULL, NULL, 'ACTIVE', 1, 2, '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),
('D003', 'FIN', '財務部', NULL, NULL, 'ACTIVE', 1, 3, '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),
('D004', 'HR', '人力資源部', NULL, NULL, 'ACTIVE', 1, 4, '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),
('D005', 'RD-FE', '前端組', 'D001', NULL, 'ACTIVE', 2, 1, '2025-01-01 09:00:00', '2025-01-01 09:00:00', 0),
('D006', 'RD-BE', '後端組', 'D001', NULL, 'INACTIVE', 2, 2, '2025-01-01 09:00:00', '2025-01-20 09:00:00', 0);

-- 員工測試資料 (共 15 筆)
-- 狀態分布: ACTIVE=8, PROBATION=3, TERMINATED=2, UNPAID_LEAVE=1, PARENTAL_LEAVE=1
-- 部門分布: D001=5, D002=4, D003=3, D004=3
-- 雇用類型: REGULAR=10, PROBATION=3, CONTRACT=2
INSERT INTO employees (employee_id, employee_number, first_name, last_name, full_name, national_id, date_of_birth, gender, company_email, mobile_phone, organization_id, department_id, job_title, job_level, employment_type, employment_status, hire_date, probation_end_date, termination_date, termination_reason, created_at, updated_at, is_deleted) VALUES
-- ACTIVE 員工 (8 筆)
('E001', 'EMP202501-001', '大明', '王', '王大明', 'A123456789', '1990-05-15', 'MALE', 'wang.daming@company.com', '0912345678', 'ORG001', 'D001', '資深工程師', 'S3', 'REGULAR', 'ACTIVE', '2020-03-01', '2020-06-01', NULL, NULL, '2020-03-01 09:00:00', '2025-01-01 09:00:00', 0),
('E002', 'EMP202501-002', '小美', '李', '李小美', 'B234567890', '1992-08-20', 'FEMALE', 'li.xiaomei@company.com', '0923456789', 'ORG001', 'D001', '工程師', 'S2', 'REGULAR', 'ACTIVE', '2021-06-01', '2021-09-01', NULL, NULL, '2021-06-01 09:00:00', '2025-01-01 09:00:00', 0),
('E003', 'EMP202501-003', '志偉', '張', '張志偉', 'C345678901', '1988-12-10', 'MALE', 'zhang.zhiwei@company.com', '0934567890', 'ORG001', 'D002', '業務經理', 'M2', 'REGULAR', 'ACTIVE', '2019-01-15', '2019-04-15', NULL, NULL, '2019-01-15 09:00:00', '2025-01-01 09:00:00', 0),
('E004', 'EMP202501-004', '雅婷', '陳', '陳雅婷', 'D456789012', '1995-03-25', 'FEMALE', 'chen.yating@company.com', '0945678901', 'ORG001', 'D002', '業務專員', 'S1', 'REGULAR', 'ACTIVE', '2022-02-01', '2022-05-01', NULL, NULL, '2022-02-01 09:00:00', '2025-01-01 09:00:00', 0),
('E005', 'EMP202501-005', '建宏', '林', '林建宏', 'E567890123', '1985-07-08', 'MALE', 'lin.jianhong@company.com', '0956789012', 'ORG001', 'D003', '財務經理', 'M2', 'REGULAR', 'ACTIVE', '2018-06-01', '2018-09-01', NULL, NULL, '2018-06-01 09:00:00', '2025-01-01 09:00:00', 0),
('E006', 'EMP202501-006', '淑芬', '黃', '黃淑芬', 'F678901234', '1993-11-30', 'FEMALE', 'huang.shufen@company.com', '0967890123', 'ORG001', 'D003', '會計師', 'S2', 'REGULAR', 'ACTIVE', '2021-09-01', '2021-12-01', NULL, NULL, '2021-09-01 09:00:00', '2025-01-01 09:00:00', 0),
('E007', 'EMP202501-007', '家豪', '吳', '吳家豪', 'G789012345', '1991-02-14', 'MALE', 'wu.jiahao@company.com', '0978901234', 'ORG001', 'D004', 'HR 主管', 'M1', 'REGULAR', 'ACTIVE', '2020-08-01', '2020-11-01', NULL, NULL, '2020-08-01 09:00:00', '2025-01-01 09:00:00', 0),
('E008', 'EMP202501-008', '怡君', '周', '周怡君', 'H890123456', '1994-06-18', 'FEMALE', 'zhou.yijun@company.com', '0989012345', 'ORG001', 'D004', 'HR 專員', 'S1', 'REGULAR', 'ACTIVE', '2023-01-01', '2023-04-01', NULL, NULL, '2023-01-01 09:00:00', '2025-01-01 09:00:00', 0),

-- PROBATION 試用期員工 (3 筆)
('E009', 'EMP202501-009', '俊傑', '劉', '劉俊傑', 'I901234567', '1996-09-22', 'MALE', 'liu.junjie@company.com', '0990123456', 'ORG001', 'D001', '初級工程師', 'J1', 'REGULAR', 'PROBATION', '2025-01-02', '2025-04-02', NULL, NULL, '2025-01-02 09:00:00', '2025-01-02 09:00:00', 0),
('E010', 'EMP202501-010', '佳慧', '蔡', '蔡佳慧', 'J012345678', '1997-04-05', 'FEMALE', 'cai.jiahui@company.com', '0901234567', 'ORG001', 'D002', '業務助理', 'J1', 'REGULAR', 'PROBATION', '2025-01-10', '2025-04-10', NULL, NULL, '2025-01-10 09:00:00', '2025-01-10 09:00:00', 0),
('E011', 'EMP202501-011', '明輝', '許', '許明輝', 'K123456789', '1998-01-28', 'MALE', 'xu.minghui@company.com', '0912345670', 'ORG001', 'D001', '實習生', 'I1', 'CONTRACT', 'PROBATION', '2025-01-15', '2025-04-15', NULL, NULL, '2025-01-15 09:00:00', '2025-01-15 09:00:00', 0),

-- TERMINATED 離職員工 (2 筆)
('E012', 'EMP202501-012', '文傑', '鄭', '鄭文傑', 'L234567890', '1989-10-12', 'MALE', 'zheng.wenjie@company.com', '0923456780', 'ORG001', 'D001', '工程師', 'S2', 'REGULAR', 'TERMINATED', '2019-05-01', '2019-08-01', '2024-12-31', '個人生涯規劃', '2019-05-01 09:00:00', '2024-12-31 09:00:00', 0),
('E013', 'EMP202501-013', '佩琪', '謝', '謝佩琪', 'M345678901', '1990-07-20', 'FEMALE', 'xie.peiqi@company.com', '0934567801', 'ORG001', 'D002', '業務專員', 'S1', 'REGULAR', 'TERMINATED', '2020-03-01', '2020-06-01', '2024-11-30', '轉職', '2020-03-01 09:00:00', '2024-11-30 09:00:00', 0),

-- UNPAID_LEAVE 留職停薪 (1 筆)
('E014', 'EMP202501-014', '宗翰', '楊', '楊宗翰', 'N456789012', '1987-05-08', 'MALE', 'yang.zonghan@company.com', '0945678912', 'ORG001', 'D003', '資深會計', 'S3', 'REGULAR', 'UNPAID_LEAVE', '2017-08-01', '2017-11-01', NULL, NULL, '2017-08-01 09:00:00', '2025-01-01 09:00:00', 0),

-- PARENTAL_LEAVE 育嬰留停 (1 筆)
('E015', 'EMP202501-015', '雅琳', '郭', '郭雅琳', 'O567890123', '1991-12-25', 'FEMALE', 'guo.yalin@company.com', '0956789023', 'ORG001', 'D004', 'HR 專員', 'S1', 'REGULAR', 'PARENTAL_LEAVE', '2019-11-01', '2020-02-01', NULL, NULL, '2019-11-01 09:00:00', '2025-01-01 09:00:00', 0);

-- 測試場景說明:
-- ORG_E001: 查詢 ACTIVE 員工 → 預期 8 筆
-- ORG_E002: 查詢 TERMINATED 員工 → 預期 2 筆
-- ORG_E003: 查詢部門 D001 員工 → 預期 5 筆 (含各種狀態)
-- ORG_E004: 依姓名模糊查詢 '王' → 預期 1 筆
-- ORG_E007: 查詢 PROBATION 試用期 → 預期 3 筆
-- ORG_E011: 查詢 2025-01-01 之後到職 → 預期 3 筆
-- ORG_E012: 查詢 UNPAID_LEAVE → 預期 1 筆
-- ORG_D001: 查詢 ACTIVE 部門 → 預期 5 筆
-- ORG_D002: 查詢頂層部門 (parent_id IS NULL) → 預期 4 筆
-- ORG_D003: 查詢 D001 的子部門 → 預期 2 筆
