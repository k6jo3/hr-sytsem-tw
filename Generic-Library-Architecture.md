# 泛型程式庫架構規範 (Generic Library Architecture)

**版本:** 1.0
**建立日期:** 2025-12-18
**目的:** 透過泛型基類減少前後端重複程式碼，同時維持 SRP 單一職責原則

---

## 目錄

1. [設計原則](#1-設計原則)
2. [後端泛型架構](#2-後端泛型架構)
3. [前端泛型架構](#3-前端泛型架構)
4. [MapStruct 自動映射](#4-mapstruct-自動映射)
5. [使用範例](#5-使用範例)
6. [遷移指南](#6-遷移指南)
7. [附錄](#附錄)

---

## 1. 設計原則

### 1.1 核心理念

```
┌─────────────────────────────────────────────────────────────────┐
│                    泛型架構設計原則                              │
├─────────────────────────────────────────────────────────────────┤
│  ✅ 維持 SRP：每個 Service 仍只負責單一操作                      │
│  ✅ 減少重複：共用邏輯抽取至泛型基類                             │
│  ✅ 保留彈性：提供 Hook Method 供子類覆寫                        │
│  ✅ 向後相容：與現有 Service Factory Pattern 100% 相容           │
│  ✅ 漸進導入：可逐步遷移，不影響既有程式碼                       │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 架構總覽

```
hrms-common/
├── base/
│   ├── entity/
│   │   ├── BaseEntity.java              # 基礎實體 (審計欄位)
│   │   ├── AggregateRoot.java           # 聚合根基類
│   │   └── ValueObject.java             # 值對象基類
│   ├── service/
│   │   ├── BaseCommandService.java      # Command 基類
│   │   ├── BaseQueryService.java        # Query 基類
│   │   └── ServiceContext.java          # 服務上下文
│   ├── repository/
│   │   └── BaseRepository.java          # 泛型 Repository
│   └── dto/
│       ├── PageRequest.java             # 分頁請求
│       └── PageResponse.java            # 分頁回應
├── mapper/
│   ├── BaseMapper.java                  # MapStruct 基礎介面
│   └── MapperConfig.java                # MapStruct 全域配置
├── event/
│   ├── DomainEvent.java                 # 領域事件基類
│   └── EventPublisher.java              # 事件發布器
└── validation/
    └── ValidationGroups.java            # 驗證群組定義
```

---

## 2. 後端泛型架構

### 2.1 基礎實體 (BaseEntity)

```java
package com.company.hrms.common.base.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<ID extends Serializable> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Version
    @Column(name = "version")
    private Long version;

    // ========== 軟刪除支援 ==========

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public boolean isActive() {
        return !this.isDeleted;
    }
}
```

### 2.2 聚合根基類 (AggregateRoot)

```java
package com.company.hrms.common.base.entity;

import com.company.hrms.common.event.DomainEvent;
import lombok.Getter;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class AggregateRoot<ID extends Serializable> extends BaseEntity<ID> {

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * 註冊領域事件（延遲發布）
     */
    protected void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
    }

    /**
     * 取得並清除所有待發布事件
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = Collections.unmodifiableList(new ArrayList<>(this.domainEvents));
        this.domainEvents.clear();
        return events;
    }

    /**
     * 檢查是否有待發布事件
     */
    public boolean hasDomainEvents() {
        return !this.domainEvents.isEmpty();
    }
}
```

### 2.3 Command 服務基類 (BaseCommandService)

```java
package com.company.hrms.common.base.service;

import com.company.hrms.common.base.entity.AggregateRoot;
import com.company.hrms.common.event.DomainEvent;
import com.company.hrms.common.event.EventPublisher;
import com.company.hrms.iam.model.JWTModel;
import com.company.hrms.iam.service.CommandApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Command 服務泛型基類
 * <p>
 * 提供共用功能：
 * - 領域事件發布
 * - 審計日誌
 * - 異常處理包裝
 * - Hook Methods 供子類擴展
 *
 * @param <REQ> Request DTO 型別
 * @param <RES> Response DTO 型別
 */
@Slf4j
public abstract class BaseCommandService<REQ, RES> implements CommandApiService<REQ, RES> {

    @Autowired
    protected EventPublisher eventPublisher;

    // ========== 主要執行流程 ==========

    @Override
    public final RES execCommand(REQ request, JWTModel currentUser, String... args) throws Exception {
        // 1. 前置處理 Hook
        beforeExecute(request, currentUser);

        try {
            // 2. 執行核心邏輯（子類實作）
            RES response = doExecute(request, currentUser, args);

            // 3. 後置處理 Hook
            afterExecute(request, response, currentUser);

            return response;

        } catch (Exception e) {
            // 4. 異常處理 Hook
            onError(request, currentUser, e);
            throw e;
        }
    }

    // ========== 子類必須實作 ==========

    /**
     * 核心業務邏輯 - 子類實作
     */
    protected abstract RES doExecute(REQ request, JWTModel currentUser, String... args) throws Exception;

    // ========== Hook Methods (可選覆寫) ==========

    /**
     * 前置處理 - 可用於參數驗證、權限檢查
     */
    protected void beforeExecute(REQ request, JWTModel currentUser) {
        log.debug("[{}] beforeExecute - user: {}", getServiceName(), currentUser.getUserId());
    }

    /**
     * 後置處理 - 可用於快取清除、通知發送
     */
    protected void afterExecute(REQ request, RES response, JWTModel currentUser) {
        log.debug("[{}] afterExecute - user: {}", getServiceName(), currentUser.getUserId());
    }

    /**
     * 異常處理 - 可用於錯誤日誌、告警
     */
    protected void onError(REQ request, JWTModel currentUser, Exception e) {
        log.error("[{}] onError - user: {}, error: {}",
            getServiceName(), currentUser.getUserId(), e.getMessage(), e);
    }

    // ========== 共用工具方法 ==========

    /**
     * 發布單一領域事件
     */
    protected void publishEvent(DomainEvent event) {
        eventPublisher.publish(event);
        log.info("[{}] Event published: {}", getServiceName(), event.getClass().getSimpleName());
    }

    /**
     * 發布聚合根的所有領域事件
     */
    protected void publishEvents(AggregateRoot<?> aggregateRoot) {
        List<DomainEvent> events = aggregateRoot.pullDomainEvents();
        events.forEach(this::publishEvent);
    }

    /**
     * 取得服務名稱（用於日誌）
     */
    protected String getServiceName() {
        return this.getClass().getSimpleName();
    }
}
```

### 2.4 Query 服務基類 (BaseQueryService)

```java
package com.company.hrms.common.base.service;

import com.company.hrms.common.base.dto.PageRequest;
import com.company.hrms.common.base.dto.PageResponse;
import com.company.hrms.iam.model.JWTModel;
import com.company.hrms.iam.service.QueryApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Query 服務泛型基類
 * <p>
 * 提供共用功能：
 * - 分頁查詢封裝
 * - 快取支援 (可選)
 * - 查詢日誌
 *
 * @param <REQ> Request DTO 型別
 * @param <RES> Response DTO 型別
 */
@Slf4j
public abstract class BaseQueryService<REQ, RES> implements QueryApiService<REQ, RES> {

    // ========== 主要執行流程 ==========

    @Override
    public final RES getResponse(REQ request, JWTModel currentUser) throws Exception {
        long startTime = System.currentTimeMillis();

        try {
            // 1. 前置處理
            beforeQuery(request, currentUser);

            // 2. 嘗試從快取取得 (可選)
            RES cachedResult = getFromCache(request, currentUser);
            if (cachedResult != null) {
                log.debug("[{}] Cache hit", getServiceName());
                return cachedResult;
            }

            // 3. 執行查詢
            RES response = doQuery(request, currentUser);

            // 4. 存入快取 (可選)
            putToCache(request, currentUser, response);

            // 5. 後置處理
            afterQuery(request, response, currentUser);

            return response;

        } finally {
            long elapsed = System.currentTimeMillis() - startTime;
            log.debug("[{}] Query completed in {}ms", getServiceName(), elapsed);
        }
    }

    // ========== 子類必須實作 ==========

    /**
     * 核心查詢邏輯 - 子類實作
     */
    protected abstract RES doQuery(REQ request, JWTModel currentUser) throws Exception;

    // ========== Hook Methods (可選覆寫) ==========

    protected void beforeQuery(REQ request, JWTModel currentUser) {
        // 可覆寫：參數驗證、權限檢查
    }

    protected void afterQuery(REQ request, RES response, JWTModel currentUser) {
        // 可覆寫：結果加工、統計記錄
    }

    /**
     * 快取讀取 - 預設不啟用，子類可覆寫
     */
    protected RES getFromCache(REQ request, JWTModel currentUser) {
        return null;
    }

    /**
     * 快取寫入 - 預設不啟用，子類可覆寫
     */
    protected void putToCache(REQ request, JWTModel currentUser, RES response) {
        // 子類可覆寫實作快取邏輯
    }

    // ========== 共用工具方法 ==========

    /**
     * 將 Spring Page 轉換為自訂 PageResponse
     */
    protected <T> PageResponse<T> toPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
            .content(page.getContent())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }

    /**
     * 建立 Spring Pageable
     */
    protected Pageable toPageable(PageRequest request) {
        return org.springframework.data.domain.PageRequest.of(
            request.getPage(),
            request.getSize(),
            request.getSort() != null ? request.getSort() : org.springframework.data.domain.Sort.unsorted()
        );
    }

    protected String getServiceName() {
        return this.getClass().getSimpleName();
    }
}
```

### 2.5 分頁 DTO

```java
// PageRequest.java
package com.company.hrms.common.base.dto;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class PageRequest {
    private int page = 0;
    private int size = 20;
    private Sort sort;

    public static PageRequest of(int page, int size) {
        PageRequest req = new PageRequest();
        req.setPage(page);
        req.setSize(size);
        return req;
    }
}

// PageResponse.java
package com.company.hrms.common.base.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
```

### 2.6 領域事件基類

```java
package com.company.hrms.common.event;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class DomainEvent {

    private final String eventId;
    private final LocalDateTime occurredAt;
    private final String eventType;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredAt = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }

    /**
     * 聚合根 ID - 子類實作
     */
    public abstract String getAggregateId();

    /**
     * 聚合根類型 - 子類實作
     */
    public abstract String getAggregateType();
}
```

### 2.7 事件發布器

```java
package com.company.hrms.common.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    /**
     * 發布領域事件（本地 + Kafka）
     */
    public void publish(DomainEvent event) {
        // 1. 本地事件（同 JVM 內的 @EventListener 可接收）
        applicationEventPublisher.publishEvent(event);

        // 2. Kafka 事件（跨服務）
        String topic = resolveTopicName(event);
        kafkaTemplate.send(topic, event.getAggregateId(), event);

        log.info("Event published: {} -> topic: {}", event.getEventType(), topic);
    }

    /**
     * 僅發布本地事件
     */
    public void publishLocal(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    private String resolveTopicName(DomainEvent event) {
        // 格式: {aggregateType}.{eventType} -> user.UserCreatedEvent
        return String.format("%s.%s",
            event.getAggregateType().toLowerCase(),
            event.getEventType());
    }
}
```

---

## 3. 前端泛型架構

### 3.1 架構總覽

```
src/shared/
├── api/
│   ├── createApi.ts                 # API 工廠函數
│   ├── ApiClient.ts                 # Axios 封裝
│   └── types.ts                     # 共用型別
├── hooks/
│   ├── useQuery.ts                  # 泛型查詢 Hook
│   ├── useMutation.ts               # 泛型變更 Hook
│   ├── useTableQuery.ts             # 表格查詢 Hook
│   └── useFormSubmit.ts             # 表單提交 Hook
├── components/
│   ├── DataTable/                   # 泛型資料表格
│   ├── FormModal/                   # 泛型表單 Modal
│   └── QueryFilter/                 # 泛型篩選器
└── factory/
    └── BaseFactory.ts               # 泛型 Factory 基類
```

### 3.2 API 工廠 (createApi)

```typescript
// src/shared/api/createApi.ts
import { AxiosInstance } from 'axios';
import { apiClient } from './ApiClient';

export interface ApiConfig<T, C = Partial<T>, U = Partial<T>> {
  baseUrl: string;
  client?: AxiosInstance;
  // Hook functions
  beforeCreate?: (data: C) => C;
  beforeUpdate?: (id: string, data: U) => U;
  transformResponse?: (data: any) => T;
  transformListResponse?: (data: any[]) => T[];
}

export interface CrudApi<T, C = Partial<T>, U = Partial<T>> {
  getList: (params?: Record<string, any>) => Promise<PageResponse<T>>;
  getById: (id: string) => Promise<T>;
  create: (data: C) => Promise<T>;
  update: (id: string, data: U) => Promise<T>;
  delete: (id: string) => Promise<void>;
  // 擴展點
  custom: <R>(method: string, path: string, data?: any) => Promise<R>;
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export function createApi<T, C = Partial<T>, U = Partial<T>>(
  config: ApiConfig<T, C, U>
): CrudApi<T, C, U> {
  const client = config.client ?? apiClient;
  const { baseUrl, beforeCreate, beforeUpdate, transformResponse, transformListResponse } = config;

  const transform = (data: any): T => {
    return transformResponse ? transformResponse(data) : data;
  };

  const transformList = (data: any[]): T[] => {
    if (transformListResponse) return transformListResponse(data);
    return data.map(transform);
  };

  return {
    async getList(params?: Record<string, any>): Promise<PageResponse<T>> {
      const response = await client.get(baseUrl, { params });
      return {
        ...response.data,
        content: transformList(response.data.content),
      };
    },

    async getById(id: string): Promise<T> {
      const response = await client.get(`${baseUrl}/${id}`);
      return transform(response.data);
    },

    async create(data: C): Promise<T> {
      const payload = beforeCreate ? beforeCreate(data) : data;
      const response = await client.post(baseUrl, payload);
      return transform(response.data);
    },

    async update(id: string, data: U): Promise<T> {
      const payload = beforeUpdate ? beforeUpdate(id, data) : data;
      const response = await client.put(`${baseUrl}/${id}`, payload);
      return transform(response.data);
    },

    async delete(id: string): Promise<void> {
      await client.delete(`${baseUrl}/${id}`);
    },

    async custom<R>(method: string, path: string, data?: any): Promise<R> {
      const url = `${baseUrl}${path}`;
      const response = await client.request({ method, url, data });
      return response.data;
    },
  };
}
```

### 3.3 泛型查詢 Hook (useTableQuery)

```typescript
// src/shared/hooks/useTableQuery.ts
import { useState, useCallback, useEffect } from 'react';
import { useQuery, UseQueryOptions } from '@tanstack/react-query';
import { CrudApi, PageResponse } from '../api/createApi';

export interface TableQueryState<T> {
  data: T[];
  loading: boolean;
  error: Error | null;
  pagination: {
    current: number;
    pageSize: number;
    total: number;
  };
  filters: Record<string, any>;
  sorter: { field: string; order: 'ascend' | 'descend' } | null;
}

export interface TableQueryActions {
  setPage: (page: number) => void;
  setPageSize: (size: number) => void;
  setFilters: (filters: Record<string, any>) => void;
  setSorter: (field: string, order: 'ascend' | 'descend' | null) => void;
  refresh: () => void;
  reset: () => void;
}

export interface UseTableQueryOptions<T> {
  api: CrudApi<T, any, any>;
  queryKey: string;
  initialPageSize?: number;
  initialFilters?: Record<string, any>;
  enabled?: boolean;
}

export function useTableQuery<T>(
  options: UseTableQueryOptions<T>
): TableQueryState<T> & TableQueryActions {
  const { api, queryKey, initialPageSize = 20, initialFilters = {}, enabled = true } = options;

  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [filters, setFilters] = useState<Record<string, any>>(initialFilters);
  const [sorter, setSorterState] = useState<{ field: string; order: 'ascend' | 'descend' } | null>(null);

  const queryParams = {
    page,
    size: pageSize,
    ...filters,
    ...(sorter ? { sort: `${sorter.field},${sorter.order === 'ascend' ? 'asc' : 'desc'}` } : {}),
  };

  const { data, isLoading, error, refetch } = useQuery<PageResponse<T>, Error>({
    queryKey: [queryKey, queryParams],
    queryFn: () => api.getList(queryParams),
    enabled,
  });

  const setSorter = useCallback((field: string, order: 'ascend' | 'descend' | null) => {
    setSorterState(order ? { field, order } : null);
    setPage(0);
  }, []);

  const handleSetFilters = useCallback((newFilters: Record<string, any>) => {
    setFilters(newFilters);
    setPage(0);
  }, []);

  const reset = useCallback(() => {
    setPage(0);
    setPageSize(initialPageSize);
    setFilters(initialFilters);
    setSorterState(null);
  }, [initialPageSize, initialFilters]);

  return {
    // State
    data: data?.content ?? [],
    loading: isLoading,
    error: error ?? null,
    pagination: {
      current: page + 1,
      pageSize,
      total: data?.totalElements ?? 0,
    },
    filters,
    sorter,
    // Actions
    setPage: (p) => setPage(p - 1),
    setPageSize,
    setFilters: handleSetFilters,
    setSorter,
    refresh: () => refetch(),
    reset,
  };
}
```

### 3.4 泛型變更 Hook (useCrudMutation)

```typescript
// src/shared/hooks/useCrudMutation.ts
import { useMutation, useQueryClient, UseMutationOptions } from '@tanstack/react-query';
import { message } from 'antd';
import { CrudApi } from '../api/createApi';

export interface CrudMutationOptions<T, C, U> {
  api: CrudApi<T, C, U>;
  queryKey: string;
  messages?: {
    createSuccess?: string;
    updateSuccess?: string;
    deleteSuccess?: string;
    error?: string;
  };
}

export interface CrudMutations<T, C, U> {
  create: {
    mutate: (data: C) => void;
    mutateAsync: (data: C) => Promise<T>;
    isLoading: boolean;
  };
  update: {
    mutate: (params: { id: string; data: U }) => void;
    mutateAsync: (params: { id: string; data: U }) => Promise<T>;
    isLoading: boolean;
  };
  remove: {
    mutate: (id: string) => void;
    mutateAsync: (id: string) => Promise<void>;
    isLoading: boolean;
  };
}

export function useCrudMutation<T, C = Partial<T>, U = Partial<T>>(
  options: CrudMutationOptions<T, C, U>
): CrudMutations<T, C, U> {
  const { api, queryKey, messages = {} } = options;
  const queryClient = useQueryClient();

  const defaultMessages = {
    createSuccess: '新增成功',
    updateSuccess: '更新成功',
    deleteSuccess: '刪除成功',
    error: '操作失敗',
    ...messages,
  };

  const invalidateQueries = () => {
    queryClient.invalidateQueries({ queryKey: [queryKey] });
  };

  const createMutation = useMutation({
    mutationFn: (data: C) => api.create(data),
    onSuccess: () => {
      message.success(defaultMessages.createSuccess);
      invalidateQueries();
    },
    onError: (error: Error) => {
      message.error(error.message || defaultMessages.error);
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: U }) => api.update(id, data),
    onSuccess: () => {
      message.success(defaultMessages.updateSuccess);
      invalidateQueries();
    },
    onError: (error: Error) => {
      message.error(error.message || defaultMessages.error);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => api.delete(id),
    onSuccess: () => {
      message.success(defaultMessages.deleteSuccess);
      invalidateQueries();
    },
    onError: (error: Error) => {
      message.error(error.message || defaultMessages.error);
    },
  });

  return {
    create: {
      mutate: createMutation.mutate,
      mutateAsync: createMutation.mutateAsync,
      isLoading: createMutation.isPending,
    },
    update: {
      mutate: updateMutation.mutate,
      mutateAsync: updateMutation.mutateAsync,
      isLoading: updateMutation.isPending,
    },
    remove: {
      mutate: deleteMutation.mutate,
      mutateAsync: deleteMutation.mutateAsync,
      isLoading: deleteMutation.isPending,
    },
  };
}
```

### 3.5 泛型 Factory 基類

```typescript
// src/shared/factory/BaseFactory.ts

/**
 * 泛型 Factory 基類
 * @template D - DTO 型別 (API Response)
 * @template V - ViewModel 型別 (UI 使用)
 */
export abstract class BaseFactory<D, V> {
  /**
   * 單一轉換 - 子類必須實作
   */
  abstract create(dto: D): V;

  /**
   * 批量轉換
   */
  createList(dtos: D[]): V[] {
    return dtos.map((dto) => this.create(dto));
  }

  /**
   * 安全轉換 (處理 null/undefined)
   */
  createSafe(dto: D | null | undefined): V | null {
    return dto ? this.create(dto) : null;
  }

  /**
   * 帶預設值轉換
   */
  createWithDefault(dto: D | null | undefined, defaultValue: V): V {
    return dto ? this.create(dto) : defaultValue;
  }
}

// ========== 使用範例 ==========

// types.ts
export interface UserDto {
  id: string;
  first_name: string;
  last_name: string;
  email: string;
  status: 'ACTIVE' | 'INACTIVE';
  role_list: string[];
  created_at: string;
}

export interface UserViewModel {
  id: string;
  fullName: string;
  email: string;
  statusLabel: string;
  statusColor: string;
  isAdmin: boolean;
  createdAt: Date;
}

// UserFactory.ts
export class UserFactory extends BaseFactory<UserDto, UserViewModel> {
  private static instance: UserFactory;

  static getInstance(): UserFactory {
    if (!UserFactory.instance) {
      UserFactory.instance = new UserFactory();
    }
    return UserFactory.instance;
  }

  create(dto: UserDto): UserViewModel {
    return {
      id: dto.id,
      fullName: `${dto.first_name} ${dto.last_name}`,
      email: dto.email,
      statusLabel: this.getStatusLabel(dto.status),
      statusColor: this.getStatusColor(dto.status),
      isAdmin: dto.role_list.includes('ADMIN'),
      createdAt: new Date(dto.created_at),
    };
  }

  private getStatusLabel(status: string): string {
    const labels: Record<string, string> = {
      ACTIVE: '在職',
      INACTIVE: '離職',
    };
    return labels[status] ?? '未知';
  }

  private getStatusColor(status: string): string {
    const colors: Record<string, string> = {
      ACTIVE: 'green',
      INACTIVE: 'red',
    };
    return colors[status] ?? 'default';
  }
}

// 使用
const userFactory = UserFactory.getInstance();
const viewModel = userFactory.create(dto);
const viewModels = userFactory.createList(dtos);
```

---

## 4. MapStruct 自動映射

### 4.1 Maven 依賴

```xml
<!-- pom.xml -->
<properties>
    <mapstruct.version>1.5.5.Final</mapstruct.version>
    <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>${lombok-mapstruct-binding.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 4.2 全域配置

```java
// MapperConfig.java
package com.company.hrms.common.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public interface MapperConfiguration {
}
```

### 4.3 基礎 Mapper 介面

```java
// BaseMapper.java
package com.company.hrms.common.mapper;

import java.util.List;

/**
 * 泛型 Mapper 基礎介面
 * @param <E> Entity
 * @param <D> DTO
 */
public interface BaseMapper<E, D> {

    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDtoList(List<E> entities);

    List<E> toEntityList(List<D> dtos);

    /**
     * 更新實體（忽略 null 值）
     */
    void updateEntityFromDto(D dto, @MappingTarget E entity);
}
```

### 4.4 使用範例

```java
// UserMapper.java
package com.company.hrms.iam.mapper;

import com.company.hrms.common.mapper.BaseMapper;
import com.company.hrms.common.mapper.MapperConfiguration;
import com.company.hrms.iam.domain.model.User;
import com.company.hrms.iam.api.request.CreateUserRequest;
import com.company.hrms.iam.api.response.UserResponse;
import org.mapstruct.*;

@Mapper(config = MapperConfiguration.class)
public interface UserMapper extends BaseMapper<User, UserResponse> {

    // Entity -> Response
    @Override
    @Mapping(target = "fullName", expression = "java(entity.getFirstName() + \" \" + entity.getLastName())")
    @Mapping(target = "statusLabel", expression = "java(entity.getStatus().getLabel())")
    UserResponse toDto(User entity);

    // CreateRequest -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User fromCreateRequest(CreateUserRequest request);

    // UpdateRequest -> Entity (更新既有實體)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(UpdateUserRequest request, @MappingTarget User entity);
}
```

---

## 5. 使用範例

### 5.1 後端完整範例

```java
// ===== Domain =====
// User.java
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends AggregateRoot<Long> {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Embedded
    private Email email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // Factory Method
    public static User create(String firstName, String lastName, Email email) {
        User user = new User();
        user.firstName = firstName;
        user.lastName = lastName;
        user.email = email;
        user.status = UserStatus.ACTIVE;

        // 註冊領域事件
        user.registerEvent(new UserCreatedEvent(user));
        return user;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.registerEvent(new UserDeactivatedEvent(this));
    }
}

// ===== Service =====
// CreateUserServiceImpl.java
@Service("createUserServiceImpl")
@RequiredArgsConstructor
public class CreateUserServiceImpl
        extends BaseCommandService<CreateUserRequest, UserResponse> {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    protected UserResponse doExecute(CreateUserRequest request, JWTModel currentUser, String... args) {
        // 1. 業務驗證
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email 已存在");
        }

        // 2. 建立領域物件
        User user = User.create(
            request.getFirstName(),
            request.getLastName(),
            new Email(request.getEmail())
        );

        // 3. 持久化
        userRepository.save(user);

        // 4. 發布領域事件
        publishEvents(user);

        // 5. 回傳
        return userMapper.toDto(user);
    }

    @Override
    protected void beforeExecute(CreateUserRequest request, JWTModel currentUser) {
        log.info("Creating user: {} by {}", request.getEmail(), currentUser.getUserId());
    }
}

// GetUserListServiceImpl.java
@Service("getUserListServiceImpl")
@RequiredArgsConstructor
public class GetUserListServiceImpl
        extends BaseQueryService<GetUserListRequest, PageResponse<UserResponse>> {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    protected PageResponse<UserResponse> doQuery(GetUserListRequest request, JWTModel currentUser) {
        // 使用 Fluent Query Engine
        Page<User> page = userRepository.findPage(request, toPageable(request));

        return PageResponse.<UserResponse>builder()
            .content(userMapper.toDtoList(page.getContent()))
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .first(page.isFirst())
            .last(page.isLast())
            .build();
    }
}
```

### 5.2 前端完整範例

```typescript
// ===== API 定義 =====
// src/features/auth/api/userApi.ts
import { createApi } from '@/shared/api/createApi';
import { UserFactory } from '../factory/UserFactory';
import { UserDto, UserViewModel, CreateUserRequest, UpdateUserRequest } from '../model/types';

export const userApi = createApi<UserViewModel, CreateUserRequest, UpdateUserRequest>({
  baseUrl: '/api/v1/users',
  transformResponse: (data: UserDto) => UserFactory.getInstance().create(data),
  transformListResponse: (data: UserDto[]) => UserFactory.getInstance().createList(data),
});

// ===== Page 使用 =====
// src/pages/HR01UserListPage.tsx
import React, { useState } from 'react';
import { Table, Button, Modal, message } from 'antd';
import { useTableQuery } from '@/shared/hooks/useTableQuery';
import { useCrudMutation } from '@/shared/hooks/useCrudMutation';
import { userApi } from '@/features/auth/api/userApi';
import { UserForm } from '@/features/auth/components/UserForm';

export const HR01UserListPage: React.FC = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [editingUser, setEditingUser] = useState<UserViewModel | null>(null);

  // 使用泛型 Hook
  const { data, loading, pagination, setPage, setFilters, refresh } = useTableQuery({
    api: userApi,
    queryKey: 'users',
  });

  const { create, update, remove } = useCrudMutation({
    api: userApi,
    queryKey: 'users',
  });

  const columns = [
    { title: '姓名', dataIndex: 'fullName', key: 'fullName' },
    { title: 'Email', dataIndex: 'email', key: 'email' },
    { title: '狀態', dataIndex: 'statusLabel', key: 'status' },
    {
      title: '操作',
      render: (_, record: UserViewModel) => (
        <>
          <Button onClick={() => handleEdit(record)}>編輯</Button>
          <Button danger onClick={() => handleDelete(record.id)}>刪除</Button>
        </>
      ),
    },
  ];

  const handleEdit = (user: UserViewModel) => {
    setEditingUser(user);
    setModalVisible(true);
  };

  const handleDelete = (id: string) => {
    Modal.confirm({
      title: '確認刪除？',
      onOk: () => remove.mutate(id),
    });
  };

  const handleSubmit = async (values: any) => {
    if (editingUser) {
      await update.mutateAsync({ id: editingUser.id, data: values });
    } else {
      await create.mutateAsync(values);
    }
    setModalVisible(false);
    setEditingUser(null);
  };

  return (
    <div>
      <Button type="primary" onClick={() => setModalVisible(true)}>
        新增使用者
      </Button>

      <Table
        dataSource={data}
        columns={columns}
        loading={loading}
        pagination={{
          ...pagination,
          onChange: setPage,
        }}
        rowKey="id"
      />

      <Modal
        title={editingUser ? '編輯使用者' : '新增使用者'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          setEditingUser(null);
        }}
        footer={null}
      >
        <UserForm
          initialValues={editingUser}
          onSubmit={handleSubmit}
          loading={create.isLoading || update.isLoading}
        />
      </Modal>
    </div>
  );
};
```

---

## 6. 遷移指南

### 6.1 漸進式遷移策略

```
┌─────────────────────────────────────────────────────────────────┐
│                    遷移階段                                      │
├─────────────────────────────────────────────────────────────────┤
│  Phase 1: 建立基礎設施 (1-2 天)                                  │
│    - 建立 hrms-common 模組                                      │
│    - 實作 BaseEntity, BaseCommandService, BaseQueryService      │
│    - 配置 MapStruct                                             │
├─────────────────────────────────────────────────────────────────┤
│  Phase 2: 試點服務 (2-3 天)                                      │
│    - 選擇一個簡單服務 (如 Training) 作為試點                     │
│    - 新功能使用泛型架構                                          │
│    - 驗證架構可行性                                              │
├─────────────────────────────────────────────────────────────────┤
│  Phase 3: 漸進擴展 (持續)                                        │
│    - 新功能強制使用泛型架構                                      │
│    - 既有程式碼按需遷移                                          │
│    - 不強制重寫已穩定的程式碼                                    │
└─────────────────────────────────────────────────────────────────┘
```

### 6.2 遷移檢查清單

**後端遷移：**

- [ ] Service 繼承 `BaseCommandService` 或 `BaseQueryService`
- [ ] 將 `execCommand()` 邏輯移至 `doExecute()`
- [ ] Entity 繼承 `BaseEntity` 或 `AggregateRoot`
- [ ] 建立 MapStruct Mapper 介面
- [ ] 移除手動 DTO 轉換程式碼

**前端遷移：**

- [ ] 使用 `createApi()` 建立 API 實例
- [ ] 使用 `useTableQuery` 取代手寫分頁邏輯
- [ ] 使用 `useCrudMutation` 取代手寫 mutation
- [ ] Factory 繼承 `BaseFactory`

---

## 附錄

### A. 效益對照表

| 指標 | 遷移前 | 遷移後 | 改善 |
|:---|:---:|:---:|:---:|
| 新增 CRUD Service | ~150 行 | ~30 行 | **80%** |
| 新增 API 端點 | 2-3 小時 | 30 分鐘 | **75%** |
| DTO 轉換程式碼 | 手寫 80 行 | MapStruct 0 行 | **100%** |
| 前端表格頁面 | ~250 行 | ~80 行 | **68%** |
| Bug 修復成本 | 分散各處 | 集中基類 | 風險降低 |

### B. 相關文件

| 文件 | 說明 |
|:---|:---|
| `Fluent-Query-Engine.md` | 查詢引擎規範 (Querydsl) |
| `backend/架構說明與開發規範.md` | 後端開發規範 |
| `frontend/架構說明與開發規範.md` | 前端開發規範 |
| `spec/系統架構設計文件.md` | 整體架構設計 |

---

**文件版本:** 1.0
**建立日期:** 2025-12-18
**維護者:** SA Team
