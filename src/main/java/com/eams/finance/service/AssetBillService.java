package com.eams.finance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eams.finance.vo.BillVO;

/**
 * 资产账单服务
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
public interface AssetBillService {

    /**
     * 生成月度账单
     *
     * @param year  年份
     * @param month 月份
     * @return 账单ID
     */
    Long generateMonthlyBill(Integer year, Integer month);

    /**
     * 生成年度账单
     *
     * @param year 年份
     * @return 账单ID
     */
    Long generateAnnualBill(Integer year);

    /**
     * 获取账单详情
     *
     * @param billId 账单ID
     * @return 账单详情
     */
    BillVO getBillDetail(Long billId);

    /**
     * 确认账单
     *
     * @param billId 账单ID
     */
    void confirmBill(Long billId);

    /**
     * 分页查询账单
     *
     * @param current  当前页
     * @param size     每页大小
     * @param billType 账单类型
     * @param year     年份
     * @param month    月份
     * @return 分页结果
     */
    Page<BillVO> getBillPage(Integer current, Integer size, Integer billType, Integer year, Integer month);
}
