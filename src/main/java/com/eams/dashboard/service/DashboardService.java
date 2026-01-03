package com.eams.dashboard.service;

import com.eams.dashboard.vo.AssetDistribution;
import com.eams.dashboard.vo.DashboardStatistics;
import com.eams.dashboard.vo.TimeTrendStatistics;

import java.time.LocalDate;
import java.util.List;

/**
 * 仪表盘数据服务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface DashboardService {

    /**
     * 获取仪表盘概览数据
     *
     * @return 统计数据
     */
    DashboardStatistics getDashboardStatistics();

    /**
     * 获取部门资产分布
     *
     * @return 分布数据列表
     */
    List<AssetDistribution> getDepartmentDistribution();

    /**
     * 获取资产分类分布
     *
     * @return 分布数据列表
     */
    List<AssetDistribution> getCategoryDistribution();

    /**
     * 获取时间趋势统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 趋势数据列表
     */
    List<TimeTrendStatistics> getTimeTrendStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 获取资产状态分布
     *
     * @return 状态分布数据
     */
    List<AssetDistribution> getStatusDistribution();
}
