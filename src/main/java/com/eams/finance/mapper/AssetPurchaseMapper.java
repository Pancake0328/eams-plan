package com.eams.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.finance.entity.AssetPurchase;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产采购记录Mapper
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface AssetPurchaseMapper extends BaseMapper<AssetPurchase> {
}
