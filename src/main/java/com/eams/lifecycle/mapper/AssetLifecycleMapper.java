package com.eams.lifecycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.lifecycle.entity.AssetLifecycle;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产生命周期Mapper
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface AssetLifecycleMapper extends BaseMapper<AssetLifecycle> {
}
