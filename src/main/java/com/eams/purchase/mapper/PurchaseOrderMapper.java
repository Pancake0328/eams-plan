package com.eams.purchase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.purchase.entity.PurchaseOrder;
import com.eams.purchase.vo.PurchaseFundOverviewVO;
import com.eams.purchase.vo.PurchaseFundStatisticVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface PurchaseOrderMapper extends BaseMapper<PurchaseOrder> {

    @Select("SELECT COALESCE(SUM(total_amount), 0) AS totalAmount, COUNT(1) AS orderCount " +
            "FROM asset_purchase_order WHERE deleted = 0 AND purchase_status <> 4")
    PurchaseFundOverviewVO selectTotalSummary();

    @Select("SELECT COALESCE(SUM(total_amount), 0) AS totalAmount, COUNT(1) AS orderCount " +
            "FROM asset_purchase_order " +
            "WHERE deleted = 0 AND purchase_status <> 4 AND purchase_date BETWEEN #{startDate} AND #{endDate}")
    PurchaseFundOverviewVO selectSummaryByDateRange(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    @Select("SELECT IFNULL(supplier, '未填写') AS dimension, COUNT(1) AS orderCount, " +
            "COALESCE(SUM(total_amount), 0) AS totalAmount " +
            "FROM asset_purchase_order WHERE deleted = 0 AND purchase_status <> 4 " +
            "GROUP BY supplier ORDER BY totalAmount DESC")
    List<PurchaseFundStatisticVO> selectSupplierStatistics();

    @Select("SELECT DATE_FORMAT(purchase_date, '%Y-%m') AS dimension, COUNT(1) AS orderCount, " +
            "COALESCE(SUM(total_amount), 0) AS totalAmount " +
            "FROM asset_purchase_order " +
            "WHERE deleted = 0 AND purchase_status <> 4 AND purchase_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(purchase_date, '%Y-%m') ORDER BY dimension")
    List<PurchaseFundStatisticVO> selectTimeStatistics(@Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
}
