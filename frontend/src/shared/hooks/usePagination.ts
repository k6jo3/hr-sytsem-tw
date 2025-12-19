import { useState, useCallback, useMemo } from 'react';
import type { PageRequest, PageResponse } from '../types';

/**
 * usePagination Hook 配置
 */
export interface UsePaginationOptions {
  /** 初始頁碼 */
  initialPage?: number;
  /** 初始每頁筆數 */
  initialSize?: number;
  /** 預設排序欄位 */
  defaultSortBy?: string;
  /** 預設排序方向 */
  defaultSortDirection?: 'ASC' | 'DESC';
}

/**
 * usePagination Hook 回傳值
 */
export interface UsePaginationReturn {
  /** 當前分頁參數 */
  pagination: PageRequest;
  /** 設定分頁參數 */
  setPagination: (params: Partial<PageRequest>) => void;
  /** 重置分頁（回到第一頁） */
  resetPagination: () => void;
  /** 從 PageResponse 建立分頁資訊 */
  getPageInfo: <T>(response: PageResponse<T> | null) => PageResponse<T>;
  /** 處理分頁變更（通常傳給 Pagination 元件） */
  handlePageChange: (params: PageRequest) => void;
}

/**
 * 分頁狀態管理 Hook
 * 提供統一的分頁狀態管理邏輯
 *
 * @example
 * ```tsx
 * const EmployeeListPage = () => {
 *   const { pagination, handlePageChange, getPageInfo, resetPagination } = usePagination({
 *     initialSize: 20,
 *     defaultSortBy: 'createdAt',
 *   });
 *
 *   const { data, isLoading } = useQuery({
 *     queryKey: ['employees', pagination],
 *     queryFn: () => EmployeeApi.getList(pagination),
 *   });
 *
 *   return (
 *     <>
 *       <SearchForm onSearch={() => resetPagination()} />
 *       <EmployeeTable data={data?.items} loading={isLoading} />
 *       <Pagination
 *         pageInfo={getPageInfo(data)}
 *         onChange={handlePageChange}
 *       />
 *     </>
 *   );
 * };
 * ```
 */
export function usePagination(options: UsePaginationOptions = {}): UsePaginationReturn {
  const {
    initialPage = 1,
    initialSize = 20,
    defaultSortBy,
    defaultSortDirection = 'DESC',
  } = options;

  const initialPagination: PageRequest = useMemo(
    () => ({
      page: initialPage,
      size: initialSize,
      sortBy: defaultSortBy,
      sortDirection: defaultSortDirection,
    }),
    [initialPage, initialSize, defaultSortBy, defaultSortDirection]
  );

  const [pagination, setPaginationState] = useState<PageRequest>(initialPagination);

  /**
   * 設定分頁參數（合併更新）
   */
  const setPagination = useCallback((params: Partial<PageRequest>) => {
    setPaginationState((prev) => ({
      ...prev,
      ...params,
    }));
  }, []);

  /**
   * 重置分頁（回到第一頁，保留其他設定）
   */
  const resetPagination = useCallback(() => {
    setPaginationState((prev) => ({
      ...prev,
      page: 1,
    }));
  }, []);

  /**
   * 從 PageResponse 取得分頁資訊，若無資料則返回預設值
   */
  const getPageInfo = useCallback(
    <T>(response: PageResponse<T> | null): PageResponse<T> => {
      if (response) {
        return response;
      }
      return {
        items: [],
        page: pagination.page ?? 1,
        size: pagination.size ?? initialSize,
        totalElements: 0,
        totalPages: 0,
        hasNext: false,
        hasPrevious: false,
      };
    },
    [pagination.page, pagination.size, initialSize]
  );

  /**
   * 處理分頁變更
   */
  const handlePageChange = useCallback(
    (params: PageRequest) => {
      setPagination(params);
    },
    [setPagination]
  );

  return {
    pagination,
    setPagination,
    resetPagination,
    getPageInfo,
    handlePageChange,
  };
}

export default usePagination;
