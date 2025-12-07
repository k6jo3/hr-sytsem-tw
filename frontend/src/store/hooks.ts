import type { AppDispatch, RootState } from '@store/index';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';

/**
 * 類型安全的 useDispatch Hook
 */
export const useAppDispatch = () => useDispatch<AppDispatch>();

/**
 * 類型安全的 useSelector Hook
 */
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
