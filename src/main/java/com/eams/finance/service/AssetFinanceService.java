package com.eams.finance.service;

import com.eams.finance.vo.FinanceStatisticsVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 资金统计服务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface AssetFinanceService {

    /**
     * 按部门统计资产金额
     *
     * @return 统计结果
     */
    List<FinanceStatisticsVO> statisticsByDepartment();

    /**
     * 按时间统计资金使用情况
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 统计结果
     */
    List<FinanceStatisticsVO> statisticsByTime(LocalDate startDate, LocalDate endDate);

    /**
     * 获取财务概览
     *
     * @return 概览统计
     */
    FinanceStatisticsVO getFinanceOverview();
}
