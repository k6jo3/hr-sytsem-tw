package com.company.hrms.organization.infrastructure.mapper;

import com.company.hrms.organization.infrastructure.po.CertificateRequestPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 證明文件申請 MyBatis Mapper
 */
@Mapper
public interface CertificateRequestMapper {

    /**
     * 根據 ID 查詢申請
     */
    CertificateRequestPO selectById(@Param("id") String id);

    /**
     * 根據員工 ID 查詢申請
     */
    List<CertificateRequestPO> selectByEmployeeId(@Param("employeeId") String employeeId);

    /**
     * 根據狀態查詢申請
     */
    List<CertificateRequestPO> selectByStatus(@Param("status") String status);

    /**
     * 根據證明類型查詢
     */
    List<CertificateRequestPO> selectByCertificateType(@Param("certificateType") String certificateType);

    /**
     * 查詢待處理的申請
     */
    List<CertificateRequestPO> selectPendingRequests();

    /**
     * 新增申請
     */
    int insert(CertificateRequestPO request);

    /**
     * 更新申請
     */
    int update(CertificateRequestPO request);

    /**
     * 刪除申請
     */
    int deleteById(@Param("id") String id);

    /**
     * 檢查申請 ID 是否存在
     */
    boolean existsById(@Param("id") String id);
}
