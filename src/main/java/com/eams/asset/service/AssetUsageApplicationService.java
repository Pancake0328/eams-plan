package com.eams.asset.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.asset.dto.UsageApplicationAuditRequest;
import com.eams.asset.dto.UsageApplicationCreateRequest;
import com.eams.asset.dto.UsageApplicationPageQuery;
import com.eams.asset.vo.UsageApplicationVO;

/**
 * 资产使用申请服务
 */
public interface AssetUsageApplicationService {

    /**
     * 创建资产使用申请
     */
    Long createApplication(UsageApplicationCreateRequest request);

    /**
     * 分页查询申请
     */
    Page<UsageApplicationVO> getApplicationPage(UsageApplicationPageQuery query);

    /**
     * 审核申请（通过/拒绝）
     */
    void auditApplication(Long id, UsageApplicationAuditRequest request);
}
