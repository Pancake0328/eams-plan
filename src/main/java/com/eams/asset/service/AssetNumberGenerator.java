package com.eams.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eams.asset.entity.AssetNumberSequence;
import com.eams.asset.mapper.AssetNumberSequenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 资产编号生成服务
 *
 * 编号规则：前缀 + 日期(可选) + 序号
 * 示例：ZC20260103001（ZC + 20260103 + 001）
 * 或：ZC0001（ZC + 0001）
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetNumberGenerator {

    private final AssetNumberSequenceMapper sequenceMapper;

    /**
     * 生成资产编号
     * 默认使用 "ZC" 前缀，不包含日期
     *
     * @return 资产编号
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateAssetNumber() {
        return generateAssetNumber("ZC", false);
    }

    /**
     * 生成资产编号
     *
     * @param prefix      编号前缀
     * @param includeDate 是否包含日期
     * @return 资产编号
     */
    @Transactional(rollbackFor = Exception.class)
    public String generateAssetNumber(String prefix, boolean includeDate) {
        // 获取日期部分（如果需要）
        String datePart = includeDate ? LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) : null;

        // 查询或创建序列记录
        LambdaQueryWrapper<AssetNumberSequence> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetNumberSequence::getPrefix, prefix);
        if (datePart != null) {
            wrapper.eq(AssetNumberSequence::getDatePart, datePart);
        } else {
            wrapper.isNull(AssetNumberSequence::getDatePart);
        }

        AssetNumberSequence sequence = sequenceMapper.selectOne(wrapper);

        if (sequence == null) {
            // 创建新序列
            sequence = new AssetNumberSequence();
            sequence.setPrefix(prefix);
            sequence.setDatePart(datePart);
            sequence.setCurrentNumber(1);
            sequenceMapper.insert(sequence);
        } else {
            // 递增序号（原子操作）
            sequenceMapper.incrementSequence(prefix, datePart);
            // 重新查询获取最新值
            sequence = sequenceMapper.selectOne(wrapper);
        }

        // 构建编号
        StringBuilder assetNumber = new StringBuilder(prefix);
        if (datePart != null) {
            assetNumber.append(datePart);
        }
        // 序号补零到4位
        assetNumber.append(String.format("%04d", sequence.getCurrentNumber()));

        log.info("生成资产编号: {}", assetNumber);
        return assetNumber.toString();
    }
}
