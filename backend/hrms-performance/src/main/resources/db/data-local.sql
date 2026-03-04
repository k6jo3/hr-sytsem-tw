-- ============================================================================
-- HR08 Performance Service - Local Seed Data (H2)
-- UUID 規則: 00000000-0000-0000-8000-00000000000x (8000 = HR08 績效)
-- 員工 UUID: 001=王大明, 002=李小美, 003=陳志強, 004=林雅婷(主管)
-- ============================================================================

-- === 員工 ReadModel（來自 HR02 Organization 的快照） ===
MERGE INTO employee_read_models (employee_id, employee_name) KEY(employee_id) VALUES
('00000000-0000-0000-0000-000000000001', '王大明'),
('00000000-0000-0000-0000-000000000002', '李小美'),
('00000000-0000-0000-0000-000000000003', '陳志強'),
('00000000-0000-0000-0000-000000000004', '林雅婷');

-- === 考核週期 (2 筆) ===
MERGE INTO performance_cycles (cycle_id, cycle_name, cycle_type, start_date, end_date, self_eval_deadline, manager_eval_deadline, status, template, created_at, updated_at) KEY(cycle_id) VALUES

-- 週期 1: 2025 年度考核（已完成）
('00000000-0000-0000-8000-000000000001', '2025年度績效考核', 'ANNUAL', '2025-01-01', '2025-12-31', '2026-01-15', '2026-01-31',
 'COMPLETED',
 '{"formName":"2025年度考核表","scoringSystem":"FIVE_POINT","forcedDistribution":true,"distributionRules":{"A":10,"B":30,"C":50,"D":10},"evaluationItems":[{"itemId":"00000000-0000-0000-8001-000000000001","itemName":"工作品質","weight":30,"description":"工作成果的品質與正確性","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":null,"selfComment":null,"managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000002","itemName":"團隊合作","weight":30,"description":"與同事協作、溝通及支援的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":null,"selfComment":null,"managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000003","itemName":"創新能力","weight":40,"description":"提出改善方案與創新思維的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":null,"selfComment":null,"managerComment":null}],"isPublished":true}',
 '2025-01-01 00:00:00', '2026-02-01 00:00:00'),

-- 週期 2: 2026 Q1 季度考核（進行中）
('00000000-0000-0000-8000-000000000002', '2026 Q1 季度考核', 'QUARTERLY', '2026-01-01', '2026-03-31', '2026-04-07', '2026-04-15',
 'IN_PROGRESS',
 '{"formName":"2026 Q1季度考核表","scoringSystem":"FIVE_POINT","forcedDistribution":false,"distributionRules":null,"evaluationItems":[{"itemId":"00000000-0000-0000-8001-000000000004","itemName":"工作品質","weight":30,"description":"工作成果的品質與正確性","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":null,"selfComment":null,"managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000005","itemName":"團隊合作","weight":30,"description":"與同事協作、溝通及支援的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":null,"selfComment":null,"managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000006","itemName":"創新能力","weight":40,"description":"提出改善方案與創新思維的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":null,"selfComment":null,"managerComment":null}],"isPublished":true}',
 '2026-01-01 00:00:00', '2026-01-15 00:00:00');


-- === 考核記錄 (6 筆：覆蓋各狀態) ===
MERGE INTO performance_reviews (review_id, cycle_id, employee_id, reviewer_id, review_type, evaluation_items, overall_score, overall_rating, final_score, final_rating, adjustment_reason, comments, status, submitted_at, finalized_at, created_at, updated_at) KEY(review_id) VALUES

-- 1. 王大明 2025年度 自評 - 已完成 (FINALIZED)
('00000000-0000-0000-8002-000000000001',
 '00000000-0000-0000-8000-000000000001',
 '00000000-0000-0000-0000-000000000001',
 '00000000-0000-0000-0000-000000000001',
 'SELF',
 '[{"itemId":"00000000-0000-0000-8001-000000000001","itemName":"工作品質","weight":30,"description":"工作成果的品質與正確性","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":4,"selfComment":"本年度完成所有專案交付，品質良好","managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000002","itemName":"團隊合作","weight":30,"description":"與同事協作、溝通及支援的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":5,"selfComment":"積極參與跨部門協作","managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000003","itemName":"創新能力","weight":40,"description":"提出改善方案與創新思維的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":4,"selfComment":"提出3項流程改善建議","managerComment":null}]',
 4.30, 'A', 4.30, 'A', NULL, '整體表現優秀',
 'FINALIZED', '2026-01-10 10:00:00', '2026-02-01 14:00:00', '2026-01-01 00:00:00', '2026-02-01 14:00:00'),

-- 2. 李小美 2025年度 自評 - 已完成 (FINALIZED)
('00000000-0000-0000-8002-000000000002',
 '00000000-0000-0000-8000-000000000001',
 '00000000-0000-0000-0000-000000000002',
 '00000000-0000-0000-0000-000000000002',
 'SELF',
 '[{"itemId":"00000000-0000-0000-8001-000000000001","itemName":"工作品質","weight":30,"description":"工作成果的品質與正確性","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":3,"selfComment":"依時完成交辦任務","managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000002","itemName":"團隊合作","weight":30,"description":"與同事協作、溝通及支援的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":4,"selfComment":"主動協助新人","managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000003","itemName":"創新能力","weight":40,"description":"提出改善方案與創新思維的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":3,"selfComment":"參與創新提案活動","managerComment":null}]',
 3.30, 'B', 3.50, 'B', '考量專案貢獻度上調', '表現穩定',
 'FINALIZED', '2026-01-12 14:00:00', '2026-02-01 15:00:00', '2026-01-01 00:00:00', '2026-02-01 15:00:00'),

-- 3. 王大明 2026 Q1 自評 - 等待自評 (PENDING_SELF)
('00000000-0000-0000-8002-000000000003',
 '00000000-0000-0000-8000-000000000002',
 '00000000-0000-0000-0000-000000000001',
 '00000000-0000-0000-0000-000000000001',
 'SELF',
 NULL,
 NULL, NULL, NULL, NULL, NULL, NULL,
 'PENDING_SELF', NULL, NULL, '2026-01-15 00:00:00', '2026-01-15 00:00:00'),

-- 4. 李小美 2026 Q1 自評 - 等待主管評 (PENDING_MANAGER)
('00000000-0000-0000-8002-000000000004',
 '00000000-0000-0000-8000-000000000002',
 '00000000-0000-0000-0000-000000000002',
 '00000000-0000-0000-0000-000000000002',
 'SELF',
 '[{"itemId":"00000000-0000-0000-8001-000000000004","itemName":"工作品質","weight":30,"description":"工作成果的品質與正確性","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":4,"selfComment":"Q1 完成兩個重要模組開發","managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000005","itemName":"團隊合作","weight":30,"description":"與同事協作、溝通及支援的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":4,"selfComment":"主動參與 Code Review","managerComment":null},{"itemId":"00000000-0000-0000-8001-000000000006","itemName":"創新能力","weight":40,"description":"提出改善方案與創新思維的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":3,"selfComment":"嘗試導入新測試框架","managerComment":null}]',
 3.60, 'B', NULL, NULL, NULL, NULL,
 'PENDING_MANAGER', '2026-03-01 16:00:00', NULL, '2026-01-15 00:00:00', '2026-03-01 16:00:00'),

-- 5. 陳志強 2026 Q1 自評 - 等待確認 (PENDING_FINALIZE)
('00000000-0000-0000-8002-000000000005',
 '00000000-0000-0000-8000-000000000002',
 '00000000-0000-0000-0000-000000000003',
 '00000000-0000-0000-0000-000000000003',
 'SELF',
 '[{"itemId":"00000000-0000-0000-8001-000000000004","itemName":"工作品質","weight":30,"description":"工作成果的品質與正確性","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":5,"selfComment":"零缺陷交付","managerComment":"品質確實優秀"},{"itemId":"00000000-0000-0000-8001-000000000005","itemName":"團隊合作","weight":30,"description":"與同事協作、溝通及支援的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":4,"selfComment":"擔任技術導師","managerComment":"指導新人表現佳"},{"itemId":"00000000-0000-0000-8001-000000000006","itemName":"創新能力","weight":40,"description":"提出改善方案與創新思維的能力","scoringCriteria":"5分:卓越 4分:優良 3分:達標 2分:待改進 1分:不合格","score":4,"selfComment":"導入自動化測試","managerComment":"效率明顯提升"}]',
 4.30, 'A', NULL, NULL, NULL, '技術能力突出，建議列為升遷候選人',
 'PENDING_FINALIZE', '2026-02-28 10:00:00', NULL, '2026-01-15 00:00:00', '2026-03-02 09:00:00'),

-- 6. 林雅婷 2026 Q1 主管評（由主管 004 評員工 002）- 等待主管評 (PENDING_MANAGER)
('00000000-0000-0000-8002-000000000006',
 '00000000-0000-0000-8000-000000000002',
 '00000000-0000-0000-0000-000000000002',
 '00000000-0000-0000-0000-000000000004',
 'MANAGER',
 NULL,
 NULL, NULL, NULL, NULL, NULL, NULL,
 'PENDING_MANAGER', NULL, NULL, '2026-01-15 00:00:00', '2026-01-15 00:00:00');
