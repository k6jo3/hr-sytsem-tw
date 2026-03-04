import { useCallback, useState } from 'react';
import { TrainingApi } from '../api';
import { TrainingViewModelFactory } from '../factory/TrainingViewModelFactory';
import type { CertificateViewModel, CourseViewModel, EnrollmentViewModel } from '../model/TrainingViewModel';

export const useTraining = () => {
  const [courses, setCourses] = useState<CourseViewModel[]>([]);
  const [myTrainings, setMyTrainings] = useState<EnrollmentViewModel[]>([]);
  const [certificates, setCertificates] = useState<CertificateViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const fetchCourses = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await TrainingApi.getCourses();
      setCourses(TrainingViewModelFactory.createCourseViewModels(res.data));
    } catch (e) {
      setError(e instanceof Error ? e : new Error('取得課程列表失敗'));
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchMyTrainings = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await TrainingApi.getMyTrainings();
      setMyTrainings(TrainingViewModelFactory.createEnrollmentViewModels(res.data));
    } catch (e) {
      setError(e instanceof Error ? e : new Error('取得我的訓練失敗'));
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchCertificates = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await TrainingApi.getCertificates();
      setCertificates(TrainingViewModelFactory.createCertificateViewModels(res.data));
    } catch (e) {
      setError(e instanceof Error ? e : new Error('取得證照列表失敗'));
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    courses,
    myTrainings,
    certificates,
    loading,
    error,
    fetchCourses,
    fetchMyTrainings,
    fetchCertificates,
  };
};
