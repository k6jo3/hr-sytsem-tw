// @ts-nocheck
/**
 * NotificationApi Adapter 單元測試
 *
 * 測試範圍：
 *   - adaptNotification：後端 NotificationItem / NotificationDetailResponse → NotificationDto
 *   - adaptTemplate：後端 TemplateItem / TemplateDetailResponse → NotificationTemplateDto
 *   - adaptAnnouncement：後端 AnnouncementItem / AnnouncementDetailResponse → AnnouncementDto
 *   - adaptPreference：後端 NotificationPreferenceResponse（巢狀結構）→ NotificationPreferenceDto
 *
 * 每個 adapter 皆測試：
 *   1. 標準後端 camelCase 輸入（正常路徑）
 *   2. 欄位正確映射（斷言每個欄位）
 *   3. null / undefined 欄位的 fallback 行為
 *   4. 未知列舉值（觸發 guardEnum 警告但不拋錯）
 *
 * 已知不一致項目（三方稽核發現，測試同步記錄）：
 *   [MISMATCH-01] adaptNotification：後端為 businessType/businessId/businessUrl，
 *                 前端讀取 relatedBusinessType/relatedBusinessId → 實際後端回應會映射失敗
 *   [MISMATCH-02] adaptNotification：NotificationItem 無 sentAt / recipientId 欄位，adapter 靜默回傳 ''
 *   [MISMATCH-03] adaptNotification：NotificationItem 的 isRead 欄位未映射至 NotificationDto
 *   [MISMATCH-04] adaptPreference：後端為巢狀 channels.emailEnabled / quietHours.startTime，
 *                 adapter 讀取平坦的 emailEnabled / quietHoursStart → 全部 fallback 至預設值
 *   [MISMATCH-05] adaptAnnouncement：後端為 expireAt，adapter 讀取 expiresAt → 會遺失值
 *   [MISMATCH-06] adaptAnnouncement：後端 publishedBy 為巢狀物件，adapter 讀取 createdBy → 映射錯誤
 *   [MISMATCH-07] SendNotificationResponse：後端回傳單一 notificationId (string)，
 *                 前端期待 notification_ids (array)，adapter 讀取 notificationIds → 回傳 []
 *   [MISMATCH-08] MarkAllAsReadResponse：後端回傳 markedCount，adapter 讀取 count → 永遠回傳 0
 *   [MISMATCH-09] AnnouncementDto status enum 缺少 'WITHDRAWN'（WithdrawAnnouncementResponse 使用）
 *   [MISMATCH-10] adaptNotification channels：運算子優先順序 bug，
 *                 `raw.channels ?? raw.channel ? [raw.channel] : ['IN_APP']` 實際解析為
 *                 `(raw.channels ?? raw.channel) ? [raw.channel] : ['IN_APP']`
 *                 → channels 陣列存在時仍回傳 [raw.channel]（undefined），而非原始陣列
 */

import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';

// ──────────────────────────────────────────────
// 取用 adapter 函式（private，透過模組重新匯出進行白箱測試）
// ──────────────────────────────────────────────

// 由於 adapter 函式在 NotificationApi.ts 中為 module-private，
// 本測試透過將 adaptXxx 函式抽離至可測試邊界的方式進行驗證。
// 目前以動態 import + eval 白箱測試方式繞過，實務上建議將 adapter 函式獨立匯出。

// 暫時：直接 inline 重新定義相同的 adapter 邏輯以進行隔離測試。
// TODO: 將 adaptNotification / adaptTemplate / adaptAnnouncement / adaptPreference
//       從 NotificationApi.ts 獨立匯出，讓測試可直接 import。

import { guardEnum } from '../../../shared/utils/adapterGuard';

// ──────────────────────────────────────────────
// 複製目前 NotificationApi.ts 中的 adapter 函式（白箱測試用）
// 若日後函式被獨立匯出，直接改為 import 即可
// ──────────────────────────────────────────────

/* eslint-disable @typescript-eslint/no-explicit-any */

function adaptNotification(raw: any) {
  return {
    notification_id: raw.notificationId ?? raw.notification_id ?? '',
    recipient_id: raw.recipientId ?? raw.recipient_id ?? '',
    title: raw.title ?? '',
    content: raw.content ?? '',
    notification_type: raw.notificationType ?? raw.notification_type ?? 'REMINDER',
    channels: raw.channels ?? raw.channel ? [raw.channel] : ['IN_APP'],
    priority: raw.priority ?? 'NORMAL',
    status: guardEnum('notification.status', raw.status, ['PENDING', 'SENT', 'FAILED', 'READ'] as const, 'SENT'),
    sent_at: raw.sentAt ?? raw.sent_at ?? '',
    read_at: raw.readAt ?? raw.read_at ?? '',
    related_business_type: raw.relatedBusinessType ?? raw.related_business_type ?? '',
    related_business_id: raw.relatedBusinessId ?? raw.related_business_id ?? '',
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

function adaptTemplate(raw: any) {
  return {
    template_id: raw.templateId ?? raw.template_id ?? '',
    template_code: raw.templateCode ?? raw.template_code ?? '',
    template_name: raw.name ?? raw.templateName ?? raw.template_name ?? '',
    subject: raw.subjectTemplate ?? raw.subject ?? '',
    body: raw.contentTemplate ?? raw.body ?? raw.content ?? '',
    default_channels: raw.defaultChannels ?? raw.default_channels ?? [],
    is_active: raw.status === 'ACTIVE' || (raw.isActive ?? raw.is_active ?? true),
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

function adaptAnnouncement(raw: any) {
  return {
    announcement_id: raw.announcementId ?? raw.announcement_id ?? '',
    title: raw.title ?? '',
    content: raw.content ?? '',
    priority: raw.priority ?? 'NORMAL',
    target_roles: raw.targetRoles ?? raw.target_roles ?? [],
    published_at: raw.publishedAt ?? raw.published_at ?? '',
    expires_at: raw.expiresAt ?? raw.expires_at ?? '',
    status: guardEnum('announcement.status', raw.status, ['DRAFT', 'PUBLISHED', 'EXPIRED', 'REVOKED'] as const, 'DRAFT'),
    created_by: raw.createdBy ?? raw.created_by ?? '',
    created_at: raw.createdAt ?? raw.created_at ?? '',
  };
}

function adaptPreference(raw: any) {
  return {
    preference_id: raw.preferenceId ?? raw.preference_id ?? '',
    employee_id: raw.employeeId ?? raw.employee_id ?? '',
    email_enabled: raw.emailEnabled ?? raw.email_enabled ?? true,
    push_enabled: raw.pushEnabled ?? raw.push_enabled ?? false,
    in_app_enabled: raw.inAppEnabled ?? raw.in_app_enabled ?? true,
    quiet_hours_start: raw.quietHoursStart ?? raw.quiet_hours_start ?? '',
    quiet_hours_end: raw.quietHoursEnd ?? raw.quiet_hours_end ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/* eslint-enable @typescript-eslint/no-explicit-any */

// ──────────────────────────────────────────────
// 測試 fixtures（模擬後端 Spring Jackson camelCase 輸出）
// ──────────────────────────────────────────────

/** 模擬 GetMyNotificationsResponse.NotificationItem（列表項目） */
const backendNotificationItem = {
  notificationId: 'ntf-001',
  title: '請假申請已核准',
  content: '您的特休假申請已核准',
  notificationType: 'APPROVAL_RESULT',
  priority: 'NORMAL',
  status: 'SENT',
  isRead: false,
  businessType: 'LEAVE_APPLICATION',   // ← 後端欄位名
  businessId: 'leave-001',             // ← 後端欄位名
  businessUrl: '/attendance/leave/applications/leave-001',
  createdAt: '2025-12-30T10:00:00',
  readAt: null,
  // 注意：列表 item 無 recipientId, sentAt
};

/** 模擬 NotificationDetailResponse（詳情） */
const backendNotificationDetail = {
  notificationId: 'ntf-002',
  title: '薪資單已產生',
  content: '您的 2026/01 薪資單已可查閱',
  notificationType: 'REMINDER',
  priority: 'HIGH',
  status: 'READ',
  channels: ['IN_APP', 'EMAIL'],
  businessType: 'PAYROLL',
  businessId: 'pay-001',
  businessUrl: '/payroll/slips/pay-001',
  createdAt: '2026-01-05T08:00:00',
  sentAt: '2026-01-05T08:00:05',
  readAt: '2026-01-05T09:00:00',
  recipientId: 'emp-001',
};

/** 模擬 TemplateListResponse.TemplateItem */
const backendTemplateItem = {
  templateId: 'tpl-001',
  templateCode: 'LEAVE_APPROVED',
  templateName: '請假申請核准通知',
  subject: '【請假核准】您的請假申請已核准',
  notificationType: 'APPROVAL_RESULT',
  defaultChannels: ['IN_APP', 'EMAIL'],
  isActive: true,
  variableCount: 6,
  createdAt: '2025-01-23T10:00:00',
  updatedAt: '2025-06-01T00:00:00',
};

/** 模擬 TemplateDetailResponse */
const backendTemplateDetail = {
  templateId: 'tpl-002',
  templateCode: 'PAYROLL_READY',
  templateName: '薪資單產生通知',
  subject: '您的薪資單已可查閱',
  body: '親愛的 {{employeeName}}，您的 {{month}} 薪資單已產生。',
  notificationType: 'REMINDER',
  defaultPriority: 'NORMAL',
  defaultChannels: ['IN_APP'],
  isActive: true,
  createdAt: '2025-03-01T00:00:00',
  createdBy: 'system',
  updatedAt: '2025-06-01T00:00:00',
  updatedBy: 'admin',
};

/** 模擬 AnnouncementListResponse.AnnouncementItem */
const backendAnnouncementItem = {
  announcementId: 'ann-001',
  title: '2026年春節放假公告',
  summary: '各位同仁：2026年春節假期為...',
  content: '各位同仁：\n\n2026年春節假期為1/28（六）至2/5（日）',
  priority: 'HIGH',
  status: 'PUBLISHED',
  isPinned: false,
  publishedAt: '2025-12-30T09:00:00',
  expireAt: '2026-02-05T23:59:59',   // ← 後端欄位名（非 expiresAt）
  isRead: false,
  publishedBy: {                       // ← 後端為巢狀物件（非 createdBy 字串）
    employeeId: 'hr-001',
    fullName: '人事部',
  },
};

/** 模擬 AnnouncementDetailResponse */
const backendAnnouncementDetail = {
  announcementId: 'ann-002',
  title: '資安教育訓練公告',
  content: '全員須於 3/31 前完成資安訓練',
  priority: 'URGENT',
  status: 'PUBLISHED',
  targetAudience: {
    type: 'ALL',
    departmentIds: [],
    roleIds: [],
  },
  publishedAt: '2026-02-01T08:00:00',
  expireAt: '2026-03-31T23:59:59',
  createdAt: '2026-01-31T17:00:00',
  publishedBy: {
    employeeId: 'hr-002',
    fullName: '資安部門',
  },
};

/** 模擬 NotificationPreferenceResponse（巢狀結構） */
const backendPreferenceNested = {
  preferenceId: 'pref-001',
  employeeId: 'emp-001',
  channels: {
    inAppEnabled: true,
    emailEnabled: true,
    pushEnabled: false,
    teamsEnabled: false,
    lineEnabled: false,
  },
  quietHours: {
    enabled: true,
    startTime: '22:00',
    endTime: '08:00',
  },
  createdAt: '2025-01-01T00:00:00',
  updatedAt: '2026-01-15T10:00:00',
};

/** 模擬假設性「平坦」偏好格式（假設後端改版後回傳平坦欄位） */
const backendPreferenceFlat = {
  preferenceId: 'pref-002',
  employeeId: 'emp-002',
  emailEnabled: false,
  pushEnabled: true,
  inAppEnabled: true,
  quietHoursStart: '23:00',
  quietHoursEnd: '07:00',
  updatedAt: '2026-02-01T00:00:00',
};

// ──────────────────────────────────────────────
// 測試套件
// ──────────────────────────────────────────────

describe('adaptNotification', () => {
  describe('正常路徑（標準後端 camelCase）', () => {
    it('應正確映射通知詳情回應的基本欄位', () => {
      const result = adaptNotification(backendNotificationDetail);

      expect(result.notification_id).toBe('ntf-002');
      expect(result.title).toBe('薪資單已產生');
      expect(result.content).toBe('您的 2026/01 薪資單已可查閱');
      expect(result.notification_type).toBe('REMINDER');
      expect(result.priority).toBe('HIGH');
      expect(result.status).toBe('READ');
      // [MISMATCH-10] channels bug：因運算子優先順序，channels 陣列存在時仍解析為 [raw.channel] (undefined)
      // TODO: 修正為 raw.channels ?? (raw.channel ? [raw.channel] : ['IN_APP'])
      expect(result.channels).toEqual([undefined]); // 目前實際行為（bug）
      expect(result.sent_at).toBe('2026-01-05T08:00:05');
      expect(result.read_at).toBe('2026-01-05T09:00:00');
      expect(result.created_at).toBe('2026-01-05T08:00:00');
      expect(result.recipient_id).toBe('emp-001');
    });

    it('應正確映射列表項目的基本欄位', () => {
      const result = adaptNotification(backendNotificationItem);

      expect(result.notification_id).toBe('ntf-001');
      expect(result.title).toBe('請假申請已核准');
      expect(result.notification_type).toBe('APPROVAL_RESULT');
      expect(result.status).toBe('SENT');
      expect(result.created_at).toBe('2025-12-30T10:00:00');
    });
  });

  describe('[MISMATCH-01] businessType 欄位名稱不一致', () => {
    it('後端回傳 businessType（非 relatedBusinessType）時 related_business_type 會是空字串', () => {
      // TODO: 修正 adapter：改為 raw.businessType ?? raw.relatedBusinessType ?? raw.related_business_type ?? ''
      const result = adaptNotification(backendNotificationItem);
      // 目前 adapter 讀取 relatedBusinessType，後端欄位是 businessType → 映射失敗
      expect(result.related_business_type).toBe(''); // MISMATCH：應為 'LEAVE_APPLICATION'
    });

    it('後端回傳 businessId（非 relatedBusinessId）時 related_business_id 會是空字串', () => {
      // TODO: 修正 adapter：改為 raw.businessId ?? raw.relatedBusinessId ?? raw.related_business_id ?? ''
      const result = adaptNotification(backendNotificationItem);
      expect(result.related_business_id).toBe(''); // MISMATCH：應為 'leave-001'
    });
  });

  describe('[MISMATCH-02] 列表項目缺少 sentAt / recipientId 欄位', () => {
    it('列表項目無 sentAt 時 sent_at 應為空字串', () => {
      const result = adaptNotification(backendNotificationItem);
      expect(result.sent_at).toBe(''); // 符合預期 fallback
    });

    it('列表項目無 recipientId 時 recipient_id 應為空字串', () => {
      const result = adaptNotification(backendNotificationItem);
      expect(result.recipient_id).toBe(''); // 符合預期 fallback
    });
  });

  describe('[MISMATCH-03] NotificationItem.isRead 未映射', () => {
    it('isRead 欄位在 NotificationDto 中不存在（adapter 未映射）', () => {
      // TODO: 若前端需要 isRead，應在 NotificationDto 新增並在 adapter 映射
      const result = adaptNotification(backendNotificationItem);
      expect((result as any).is_read).toBeUndefined(); // adapter 未映射此欄位
    });
  });

  describe('null / undefined 欄位的 fallback', () => {
    it('空物件應回傳全部 fallback 預設值', () => {
      const result = adaptNotification({});

      expect(result.notification_id).toBe('');
      expect(result.title).toBe('');
      expect(result.content).toBe('');
      expect(result.notification_type).toBe('REMINDER');
      expect(result.priority).toBe('NORMAL');
      expect(result.status).toBe('SENT');
      expect(result.channels).toEqual(['IN_APP']); // channels fallback
      expect(result.sent_at).toBe('');
      expect(result.read_at).toBe('');
      expect(result.created_at).toBe('');
    });

    it('null title 應 fallback 至空字串', () => {
      const result = adaptNotification({ title: null });
      expect(result.title).toBe('');
    });

    it('undefined status 應 fallback 至 SENT', () => {
      const result = adaptNotification({ status: undefined });
      expect(result.status).toBe('SENT');
    });

    it('null status 應 fallback 至 SENT', () => {
      const result = adaptNotification({ status: null });
      expect(result.status).toBe('SENT');
    });
  });

  describe('未知列舉值', () => {
    it('未知的 status 值應觸發 console.warn 並回傳原始值', () => {
      const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

      const result = adaptNotification({ status: 'ARCHIVED' });

      expect(result.status).toBe('ARCHIVED');
      // guardEnum 使用單一字串參數呼叫 console.warn
      expect(warnSpy).toHaveBeenCalledWith(
        expect.stringContaining('notification.status')
      );

      warnSpy.mockRestore();
    });

    it('合法的 status 值不應觸發 console.warn', () => {
      const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

      adaptNotification({ status: 'PENDING' });
      adaptNotification({ status: 'SENT' });
      adaptNotification({ status: 'FAILED' });
      adaptNotification({ status: 'READ' });

      expect(warnSpy).not.toHaveBeenCalled();
      warnSpy.mockRestore();
    });
  });

  describe('snake_case 輸入（前端自有格式 fallback）', () => {
    it('應接受 snake_case 欄位作為備援', () => {
      const result = adaptNotification({
        notification_id: 'ntf-snake',
        notification_type: 'ALERT',
        sent_at: '2026-01-01T00:00:00',
        created_at: '2026-01-01T00:00:00',
        status: 'PENDING',
      });

      expect(result.notification_id).toBe('ntf-snake');
      expect(result.notification_type).toBe('ALERT');
      expect(result.sent_at).toBe('2026-01-01T00:00:00');
    });
  });
});

// ──────────────────────────────────────────────

describe('adaptTemplate', () => {
  describe('正常路徑（標準後端 camelCase）', () => {
    it('應正確映射範本列表項目的所有欄位', () => {
      const result = adaptTemplate(backendTemplateItem);

      expect(result.template_id).toBe('tpl-001');
      expect(result.template_code).toBe('LEAVE_APPROVED');
      expect(result.template_name).toBe('請假申請核准通知');
      expect(result.subject).toBe('【請假核准】您的請假申請已核准');
      expect(result.default_channels).toEqual(['IN_APP', 'EMAIL']);
      expect(result.is_active).toBe(true);
      expect(result.created_at).toBe('2025-01-23T10:00:00');
    });

    it('應正確映射範本詳情的 body 欄位', () => {
      const result = adaptTemplate(backendTemplateDetail);

      expect(result.template_id).toBe('tpl-002');
      expect(result.template_code).toBe('PAYROLL_READY');
      expect(result.body).toBe('親愛的 {{employeeName}}，您的 {{month}} 薪資單已產生。');
      expect(result.subject).toBe('您的薪資單已可查閱');
      expect(result.is_active).toBe(true);
    });
  });

  describe('is_active 推導邏輯', () => {
    it('status = ACTIVE 時 is_active 應為 true（即使 isActive = false）', () => {
      const result = adaptTemplate({ status: 'ACTIVE', isActive: false });
      expect(result.is_active).toBe(true);
    });

    it('status = INACTIVE 且 isActive = true 時 is_active 應為 true（isActive 優先）', () => {
      const result = adaptTemplate({ status: 'INACTIVE', isActive: true });
      expect(result.is_active).toBe(true);
    });

    it('status = INACTIVE 且 isActive = false 時 is_active 應為 false', () => {
      const result = adaptTemplate({ status: 'INACTIVE', isActive: false });
      expect(result.is_active).toBe(false);
    });

    it('無 status 且無 isActive 時 is_active 預設為 true', () => {
      const result = adaptTemplate({});
      expect(result.is_active).toBe(true);
    });
  });

  describe('template_name 映射優先順序', () => {
    it('應優先使用 name 欄位（部分後端可能回傳 name）', () => {
      const result = adaptTemplate({ name: '以 name 命名', templateName: '以 templateName 命名' });
      expect(result.template_name).toBe('以 name 命名');
    });

    it('無 name 時應使用 templateName', () => {
      const result = adaptTemplate({ templateName: '以 templateName 命名' });
      expect(result.template_name).toBe('以 templateName 命名');
    });
  });

  describe('subject 映射優先順序', () => {
    it('應優先使用 subjectTemplate 欄位', () => {
      const result = adaptTemplate({ subjectTemplate: 'subjectTemplate 值', subject: 'subject 值' });
      expect(result.subject).toBe('subjectTemplate 值');
    });
  });

  describe('body 映射優先順序', () => {
    it('應優先使用 contentTemplate 欄位', () => {
      const result = adaptTemplate({ contentTemplate: '內容模板', body: '備用 body', content: '備用 content' });
      expect(result.body).toBe('內容模板');
    });

    it('無 contentTemplate 時應使用 body', () => {
      const result = adaptTemplate({ body: '備用 body', content: '備用 content' });
      expect(result.body).toBe('備用 body');
    });
  });

  describe('null / undefined 欄位的 fallback', () => {
    it('空物件應回傳全部 fallback 預設值', () => {
      const result = adaptTemplate({});

      expect(result.template_id).toBe('');
      expect(result.template_code).toBe('');
      expect(result.template_name).toBe('');
      expect(result.subject).toBe('');
      expect(result.body).toBe('');
      expect(result.default_channels).toEqual([]);
      expect(result.is_active).toBe(true);
      expect(result.created_at).toBe('');
    });
  });
});

// ──────────────────────────────────────────────

describe('adaptAnnouncement', () => {
  describe('正常路徑（標準後端 camelCase）', () => {
    it('應正確映射公告列表項目的基本欄位', () => {
      const result = adaptAnnouncement(backendAnnouncementItem);

      expect(result.announcement_id).toBe('ann-001');
      expect(result.title).toBe('2026年春節放假公告');
      expect(result.content).toBe('各位同仁：\n\n2026年春節假期為1/28（六）至2/5（日）');
      expect(result.priority).toBe('HIGH');
      expect(result.status).toBe('PUBLISHED');
      expect(result.published_at).toBe('2025-12-30T09:00:00');
    });
  });

  describe('[MISMATCH-05] expireAt vs expiresAt 欄位名稱不一致', () => {
    it('後端回傳 expireAt（非 expiresAt）時 expires_at 應為空字串', () => {
      // TODO: 修正 adapter：改為 raw.expireAt ?? raw.expiresAt ?? raw.expires_at ?? ''
      const result = adaptAnnouncement(backendAnnouncementItem);
      expect(result.expires_at).toBe(''); // MISMATCH：應為 '2026-02-05T23:59:59'
    });

    it('後端使用 expiresAt 時應能正確映射（假設後端改版）', () => {
      const result = adaptAnnouncement({ ...backendAnnouncementItem, expiresAt: '2026-02-05T23:59:59' });
      expect(result.expires_at).toBe('2026-02-05T23:59:59');
    });
  });

  describe('[MISMATCH-06] publishedBy 巢狀物件映射錯誤', () => {
    it('後端回傳巢狀 publishedBy 物件時 created_by 應為空字串', () => {
      // TODO: 修正 adapter：改為 raw.publishedBy?.employeeId ?? raw.createdBy ?? raw.created_by ?? ''
      const result = adaptAnnouncement(backendAnnouncementItem);
      expect(result.created_by).toBe(''); // MISMATCH：應為 'hr-001'（publishedBy.employeeId）
    });

    it('後端回傳 createdBy 字串時能正確映射', () => {
      const result = adaptAnnouncement({ createdBy: 'hr-system' });
      expect(result.created_by).toBe('hr-system');
    });
  });

  describe('target_roles 映射', () => {
    it('後端 targetRoles 陣列應正確映射', () => {
      const result = adaptAnnouncement({ targetRoles: ['HR_MANAGER', 'DEPT_HEAD'] });
      expect(result.target_roles).toEqual(['HR_MANAGER', 'DEPT_HEAD']);
    });

    it('無 targetRoles 時應 fallback 至空陣列', () => {
      const result = adaptAnnouncement({});
      expect(result.target_roles).toEqual([]);
    });
  });

  describe('[MISMATCH-09] 未知 status 值：WITHDRAWN', () => {
    it('status = WITHDRAWN 應觸發 console.warn 並回傳原始值（WITHDRAWN 不在前端 enum 中）', () => {
      // TODO: 在 AnnouncementDto status 中新增 'WITHDRAWN'
      const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

      const result = adaptAnnouncement({ status: 'WITHDRAWN' });

      expect(result.status).toBe('WITHDRAWN'); // guardEnum 回傳原始值
      // guardEnum 使用單一字串參數呼叫 console.warn
      expect(warnSpy).toHaveBeenCalledWith(
        expect.stringContaining('announcement.status')
      );

      warnSpy.mockRestore();
    });
  });

  describe('未知列舉值', () => {
    it('完全未知的 status 應觸發警告並回傳原始值', () => {
      const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

      const result = adaptAnnouncement({ status: 'ARCHIVED' });

      expect(result.status).toBe('ARCHIVED');
      expect(warnSpy).toHaveBeenCalledWith(expect.stringContaining('announcement.status'));

      warnSpy.mockRestore();
    });

    it('合法的 status 值不應觸發警告', () => {
      const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

      adaptAnnouncement({ status: 'DRAFT' });
      adaptAnnouncement({ status: 'PUBLISHED' });
      adaptAnnouncement({ status: 'EXPIRED' });
      adaptAnnouncement({ status: 'REVOKED' });

      expect(warnSpy).not.toHaveBeenCalled();
      warnSpy.mockRestore();
    });
  });

  describe('null / undefined 欄位的 fallback', () => {
    it('空物件應回傳全部 fallback 預設值', () => {
      const result = adaptAnnouncement({});

      expect(result.announcement_id).toBe('');
      expect(result.title).toBe('');
      expect(result.content).toBe('');
      expect(result.priority).toBe('NORMAL');
      expect(result.target_roles).toEqual([]);
      expect(result.published_at).toBe('');
      expect(result.expires_at).toBe('');
      expect(result.status).toBe('DRAFT');
      expect(result.created_by).toBe('');
      expect(result.created_at).toBe('');
    });

    it('null status 應 fallback 至 DRAFT', () => {
      const result = adaptAnnouncement({ status: null });
      expect(result.status).toBe('DRAFT');
    });
  });
});

// ──────────────────────────────────────────────

describe('adaptPreference', () => {
  describe('[MISMATCH-04] 後端巢狀結構 vs adapter 平坦讀取', () => {
    it('後端回傳巢狀 channels 物件時 email_enabled 應 fallback 至預設 true（映射失敗）', () => {
      // TODO: 修正 adapter 讀取巢狀結構：
      //   email_enabled: raw.channels?.emailEnabled ?? raw.emailEnabled ?? raw.email_enabled ?? true
      const result = adaptPreference(backendPreferenceNested);
      expect(result.email_enabled).toBe(true); // MISMATCH：fallback true 與後端值 true 碰巧相同，但讀取路徑錯誤
    });

    it('後端回傳巢狀 channels.pushEnabled = false 時 push_enabled 仍 fallback 至預設 false', () => {
      // 雖然結果相同，但來源路徑錯誤——若 pushEnabled 為 true 就會出錯
      const result = adaptPreference(backendPreferenceNested);
      expect(result.push_enabled).toBe(false); // 偶然正確，但來源是 fallback 預設值
    });

    it('後端 channels.pushEnabled = true 時 adapter 無法讀取（會 fallback 至 false）', () => {
      // TODO: 修正 adapter 使其讀取 raw.channels?.pushEnabled
      const nestedWithPushTrue = {
        ...backendPreferenceNested,
        channels: { ...backendPreferenceNested.channels, pushEnabled: true },
      };
      const result = adaptPreference(nestedWithPushTrue);
      expect(result.push_enabled).toBe(false); // MISMATCH：後端為 true，adapter 讀不到 → 回傳 false
    });

    it('後端回傳巢狀 quietHours.startTime 時 quiet_hours_start 應 fallback 至空字串', () => {
      // TODO: 修正 adapter：raw.quietHours?.startTime ?? raw.quietHoursStart ?? raw.quiet_hours_start ?? ''
      const result = adaptPreference(backendPreferenceNested);
      expect(result.quiet_hours_start).toBe(''); // MISMATCH：應為 '22:00'
    });

    it('後端回傳巢狀 quietHours.endTime 時 quiet_hours_end 應 fallback 至空字串', () => {
      // TODO: 修正 adapter：raw.quietHours?.endTime ?? raw.quietHoursEnd ?? raw.quiet_hours_end ?? ''
      const result = adaptPreference(backendPreferenceNested);
      expect(result.quiet_hours_end).toBe(''); // MISMATCH：應為 '08:00'
    });
  });

  describe('正常路徑（平坦 camelCase，假設後端改版）', () => {
    it('平坦欄位應正確映射所有偏好設定', () => {
      const result = adaptPreference(backendPreferenceFlat);

      expect(result.preference_id).toBe('pref-002');
      expect(result.employee_id).toBe('emp-002');
      expect(result.email_enabled).toBe(false);
      expect(result.push_enabled).toBe(true);
      expect(result.in_app_enabled).toBe(true);
      expect(result.quiet_hours_start).toBe('23:00');
      expect(result.quiet_hours_end).toBe('07:00');
      expect(result.updated_at).toBe('2026-02-01T00:00:00');
    });
  });

  describe('null / undefined 欄位的 fallback', () => {
    it('空物件應回傳全部 fallback 預設值', () => {
      const result = adaptPreference({});

      expect(result.preference_id).toBe('');
      expect(result.employee_id).toBe('');
      expect(result.email_enabled).toBe(true);   // 預設啟用
      expect(result.push_enabled).toBe(false);   // 預設停用
      expect(result.in_app_enabled).toBe(true);  // 預設啟用
      expect(result.quiet_hours_start).toBe('');
      expect(result.quiet_hours_end).toBe('');
      expect(result.updated_at).toBe('');
    });

    it('email_enabled = false 時不應被 truthy 覆蓋', () => {
      const result = adaptPreference({ emailEnabled: false });
      expect(result.email_enabled).toBe(false);
    });

    it('push_enabled = true 時不應被 falsy fallback 覆蓋', () => {
      const result = adaptPreference({ pushEnabled: true });
      expect(result.push_enabled).toBe(true);
    });

    it('in_app_enabled = false 時不應被 truthy 覆蓋', () => {
      const result = adaptPreference({ inAppEnabled: false });
      expect(result.in_app_enabled).toBe(false);
    });

    it('null updated_at 應 fallback 至空字串', () => {
      const result = adaptPreference({ updatedAt: null });
      expect(result.updated_at).toBe('');
    });
  });

  describe('snake_case 輸入（前端自有格式 fallback）', () => {
    it('應接受 snake_case 欄位作為備援', () => {
      const result = adaptPreference({
        preference_id: 'pref-snake',
        employee_id: 'emp-snake',
        email_enabled: false,
        push_enabled: true,
        in_app_enabled: false,
        quiet_hours_start: '20:00',
        quiet_hours_end: '06:00',
        updated_at: '2026-03-01T00:00:00',
      });

      expect(result.preference_id).toBe('pref-snake');
      expect(result.email_enabled).toBe(false);
      expect(result.push_enabled).toBe(true);
      expect(result.quiet_hours_start).toBe('20:00');
      expect(result.quiet_hours_end).toBe('06:00');
    });
  });
});

// ──────────────────────────────────────────────
// 跨 adapter 邊界測試
// ──────────────────────────────────────────────

describe('Adapter 邊界：channels 陣列', () => {
  it('[MISMATCH-10] adaptNotification：channels 陣列因 bug 無法直接回傳', () => {
    // bug：`raw.channels ?? raw.channel ? [raw.channel] : ['IN_APP']`
    // 實際解析：`(raw.channels ?? raw.channel) ? [raw.channel] : ['IN_APP']`
    // 當 raw.channels = ['EMAIL', 'TEAMS']（truthy），結果為 [raw.channel] = [undefined]
    // TODO: 修正為 raw.channels ?? (raw.channel ? [raw.channel] : ['IN_APP'])
    const result = adaptNotification({ channels: ['EMAIL', 'TEAMS'] });
    expect(result.channels).toEqual([undefined]); // 目前 bug 行為，修正後應為 ['EMAIL', 'TEAMS']
  });

  it('adaptNotification：無 channels 且無 channel 時 fallback 至 [IN_APP]', () => {
    const result = adaptNotification({ channels: undefined, channel: undefined });
    expect(result.channels).toEqual(['IN_APP']);
  });

  it('adaptTemplate：defaultChannels 陣列應直接回傳', () => {
    const result = adaptTemplate({ defaultChannels: ['IN_APP', 'LINE'] });
    expect(result.default_channels).toEqual(['IN_APP', 'LINE']);
  });

  it('adaptTemplate：無 defaultChannels 時 fallback 至空陣列', () => {
    const result = adaptTemplate({});
    expect(result.default_channels).toEqual([]);
  });
});

describe('Adapter 邊界：時間戳欄位（LocalDateTime → string）', () => {
  it('adaptNotification：後端 LocalDateTime 字串應直接作為字串回傳', () => {
    const result = adaptNotification({ createdAt: '2026-01-01T10:00:00', sentAt: '2026-01-01T10:00:05' });
    expect(result.created_at).toBe('2026-01-01T10:00:00');
    expect(result.sent_at).toBe('2026-01-01T10:00:05');
  });

  it('adaptAnnouncement：publishedAt 字串應直接作為字串回傳', () => {
    const result = adaptAnnouncement({ publishedAt: '2026-01-15T09:00:00' });
    expect(result.published_at).toBe('2026-01-15T09:00:00');
  });

  it('adaptPreference：updatedAt 字串應直接作為字串回傳', () => {
    const result = adaptPreference({ updatedAt: '2026-02-28T12:00:00' });
    expect(result.updated_at).toBe('2026-02-28T12:00:00');
  });
});
