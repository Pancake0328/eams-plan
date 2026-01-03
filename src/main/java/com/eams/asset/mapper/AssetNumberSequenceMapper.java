package com.eams.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.asset.entity.AssetNumberSequence;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 资产编号序列 Mapper 接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface AssetNumberSequenceMapper extends BaseMapper<AssetNumberSequence> {

    /**
     * 获取并增加序号（原子操作）
     *
     * @param prefix   编号前缀
     * @param datePart 日期部分
     * @return 影响行数
     */
    @Update("UPDATE asset_number_sequence SET current_number = current_number + 1 " +
            "WHERE prefix = #{prefix} AND (date_part = #{datePart} OR (date_part IS NULL AND #{datePart} IS NULL))")
    int incrementSequence(@Param("prefix") String prefix, @Param("datePart") String datePart);
}
